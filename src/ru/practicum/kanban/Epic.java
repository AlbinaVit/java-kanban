package ru.practicum.kanban;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<Subtask>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

}
