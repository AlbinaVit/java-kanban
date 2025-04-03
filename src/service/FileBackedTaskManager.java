package service;

import exseption.ManagerLoadExseption;
import exseption.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTaskManager(Path filePath) throws ManagerLoadExseption {
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

    private void loadFromFile() throws ManagerLoadExseption {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            if (lines.size() > 1) {
                for (String line : lines) {
                    fromString(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadExseption("Ошибка загрузки данных из файла: " + filePath, e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path file) throws ManagerLoadExseption {
        return new FileBackedTaskManager(file);
    }
}
