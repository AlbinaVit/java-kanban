package service;

import model.Epic;
import model.Subtask;
import model.Task;
import utils.Managers;
import utils.Status;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int idCounter = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public Task createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("model.Epic с ID " + subtask.getEpicId() + " не существует.");
        }
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpicStatus(subtask.getEpicId());
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }
        return epic;
    }

    @Override
    public List<Task> getAllTasks() {
        return List.copyOf(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return List.copyOf(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return List.copyOf(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public List<Subtask> getSubtaskByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? new ArrayList<>(epic.getSubtasks()) : new ArrayList<>();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("model.Task с ID " + task.getId() + " не существует.");
        }
        Task existingTask = tasks.get(task.getId());
        if (existingTask.getStartTime() != null && !existingTask.getStartTime().equals(task.getStartTime())
                && checkForTimeIntersection(task)) {
            throw new IllegalArgumentException("Время задачи пересекается с существующей задачей.");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("model.Subtask с ID " + subtask.getId() + " не существует.");
        }
        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (existingSubtask.getStartTime() != null && !existingSubtask.getStartTime().equals(subtask.getStartTime())
                && checkForTimeIntersection(subtask)) {
            throw new IllegalArgumentException("Время подзадачи пересекается с существующей подзадачей.");
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(existingSubtask);
            epic.addSubtask(subtask);
        }
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(existingSubtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("model.Epic с ID " + epic.getId() + " не существует.");
        }
        Epic existingEpic = epics.get(epic.getId());
        if (existingEpic.getStartTime() != null && !existingEpic.getStartTime().equals(epic.getStartTime())
                && checkForTimeIntersection(epic)) {
            throw new IllegalArgumentException("Время подзадачи пересекается с существующей подзадачей.");
        }

        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = epic.getSubtasks();
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = epicSubtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        boolean anyInProgress = epicSubtasks.stream().anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(task.getId());
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                updateEpicStatus(epic.getId());
            }
            historyManager.remove(subtask.getId());
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            historyManager.remove(epic.getId());
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
            }
            epic.clearSubtasks();
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void addExistingTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void addExistingSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    protected void addExistingEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean checkForTimeIntersection(Task newTask) {
        return prioritizedTasks.stream().anyMatch(existingTask -> {
            if (existingTask.getStartTime() == null || newTask.getStartTime() == null) {
                return false;
            }
            LocalDateTime existingEndTime = existingTask.getEndTime();
            LocalDateTime newEndTime = newTask.getEndTime();
            return existingTask.getStartTime().isBefore(newEndTime) && newTask.getStartTime().isBefore(existingEndTime);
        });
    }
}
