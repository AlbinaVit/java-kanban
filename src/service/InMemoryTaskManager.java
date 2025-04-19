package service;

import model.Epic;
import model.Subtask;
import model.Task;
import utils.Managers;

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
        if (checkForTimeIntersection(task)) {
            throw new IllegalArgumentException("Время задачи пересекается с существующими задачами.");
        }
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (checkForTimeIntersection(subtask)) {
            throw new IllegalArgumentException("Время подзадачи пересекается с существующими подзадачами.");
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("model.Epic с ID " + subtask.getEpicId() + " не существует.");
        }
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);

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
        if (checkForTimeIntersection(task)) {
            throw new IllegalArgumentException("Время задачи пересекается с существующей задачей.");
        }
        Task existingTask = tasks.get(task.getId());
        prioritizedTasks.remove(existingTask);

        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("model.Subtask с ID " + subtask.getId() + " не существует.");
        }
        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (checkForTimeIntersection(subtask)) {
            throw new IllegalArgumentException("Время подзадачи пересекается с существующей подзадачей.");
        }

        prioritizedTasks.remove(existingSubtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(existingSubtask);
            epic.addSubtask(subtask);
        }
        subtasks.put(subtask.getId(), subtask);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("model.Epic с ID " + epic.getId() + " не существует.");
        }
        Epic existingEpic = epics.get(epic.getId());

        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
            }
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
            epic.clearSubtasks();
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
        }
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
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
        prioritizedTasks.clear();
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
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        return prioritizedTasks.stream().anyMatch(existingTask -> {
            if (existingTask.getId() == newTask.getId()) {
                return false;
            }
            if (existingTask.getStartTime() == null || newTask.getStartTime() == null) {
                return false;
            }
            LocalDateTime existingEndTime = existingTask.getEndTime();
            LocalDateTime newEndTime = newTask.getEndTime();
            return existingTask.getStartTime().isBefore(newEndTime) && newTask.getStartTime().isBefore(existingEndTime);
        });
    }
}
