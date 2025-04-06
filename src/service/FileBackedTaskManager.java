package service;

import exseption.ManagerLoadExseption;
import exseption.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import utils.Status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static utils.TypeTask.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTaskManager(Path filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    private void save() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                sb.append(toString(task)).append("\n");
            }
            for (Epic epic : getAllEpics()) {
                sb.append(toString(epic)).append("\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                sb.append(toString(subtask)).append("\n");
            }
            Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных", e);
        }
    }

    private String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    subtask.getId(), subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            return String.format("%d,EPIC,%s,%s,%s,",
                    epic.getId(), epic.getName(), epic.getStatus(), epic.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s,",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription());
        }
    }

    private void loadFromFile() {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            if (lines.size() > 1) {
                for (String line : lines.subList(1, lines.size())) {
                    fromString(line);
                }
                maxIdCount();
            }
        } catch (IOException e) {
            throw new ManagerLoadExseption("Ошибка загрузки данных из файла: " + filePath, e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        return new FileBackedTaskManager(file);
    }

    private void fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        if (type.equals(TASK.name())) {
            Task task = new Task(name, description, status);
            task.setId(id);
            addExistingTask(task);
        } else if (type.equals(EPIC.name())) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            addExistingEpic(epic);
        } else if (type.equals(SUBTASK.name())) {
            int epicId = Integer.parseInt(parts[5]);
            Subtask subtask = new Subtask(name, description, epicId, status);
            subtask.setId(id);
            addExistingSubtask(subtask);
        }
    }

    public void maxIdCount() throws IOException {
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        int maxId = 0;

        for (String line : lines.subList(1, lines.size())) {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            if (id > maxId) {
                maxId = id;
            }
        }
        setIdCounter(maxId + 1);
    }
}
