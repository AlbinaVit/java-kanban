package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private Path tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() throws Exception {
        tempFile = Files.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
        return taskManager;
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("Тестирование сохранения нескольких задач")
    void testSave() throws IOException {

        taskManager.createTask(new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0)));
        taskManager.createTask(new Task("Task 2", "Description 2", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 10, 0)));
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask Description 1", epic.getId(), Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 11, 0)));

        String expectedContent = "id,type,name,status,description,epic\n" +
                "1,TASK,Task 1,NEW,Description 1,\n" +
                "2,TASK,Task 2,NEW,Description 2,\n" +
                "3,EPIC,Epic 1,NEW,Epic Description 1,\n" +
                "4,SUBTASK,Subtask 1,NEW,Subtask Description 1,3";

        assertEquals(expectedContent.trim(), Files.readString(tempFile).trim());
    }

    @Test
    @DisplayName("Тестирование загрузку и сохранения пустого файла")
    void saveAndLoadEmptyFile() {
        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    @DisplayName("Загрузка и сохранение одной задачи")
    void saveAndLoadTasks() {
        taskManager.createTask(new Task("Task1", "Description1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0)));
        Epic epic = new Epic("Epic1", "Description Epic1");
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("Subtask1", "Description Subtask1", epic.getId(), Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 10, 10)));

        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(1, taskManager.getAllSubtasks().size());
    }

    @Test
    @DisplayName("Загрузка и сохранение нескольких задач")
    void saveAndLoadMultipleTasks() {
        Task task1 = new Task("Task1", "Description1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Description2", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 10, 0));
        taskManager.createTask(task2);
        Epic epic = new Epic("Epic1", "Description Epic1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1", epic.getId(), Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 11, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2", epic.getId(), Status.DONE, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 12, 0));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(2, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(2, taskManager.getAllSubtasks().size());
    }

}