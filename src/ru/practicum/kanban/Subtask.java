package ru.practicum.kanban;

public class Subtask extends Task {

    private int epicId;
    private Status status;

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
