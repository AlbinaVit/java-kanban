package ru.practicum.kanban;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        if (epics.containsKey(subtask.getEpicId())) {
            epics.get(subtask.getEpicId()).addSubtask(subtask);
        }
        return subtask;
    }

    public Epic createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic;
    }

    public List<Task> getAllTasks() {
        return List.copyOf(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return List.copyOf(subtasks.values());
    }

    public List<Epic> getAllEpics() {
        return List.copyOf(epics.values());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
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

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id);
    }

    public void deleteEpic(int id) {
        epics.remove(id);
    }
}
