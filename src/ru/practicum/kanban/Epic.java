package ru.practicum.kanban;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Subtask> subtasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public boolean removeSubtask(Subtask subtask) {
        return subtasks.remove(subtask);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

}
