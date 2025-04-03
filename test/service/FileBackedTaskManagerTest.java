package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    private Path tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = Files.createTempFile("test", ".csv");
        manager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("Тестирование сохранения нескольких задач")
    void testSave() throws IOException {

        manager.createTask(new Task("Task 1", "Description 1", Status.NEW));
        manager.createTask(new Task("Task 2", "Description 2", Status.NEW));
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        manager.createEpic(epic);
        manager.createSubtask(new Subtask("Subtask 1", "Subtask Description 1", epic.getId(), Status.NEW));

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
        assertEquals(0, manager.getAllTasks().size());
        assertEquals(0, manager.getAllSubtasks().size());
        assertEquals(0, manager.getAllEpics().size());

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    @DisplayName("Загрузка и сохранение одной задачи")
    void saveAndLoadTasks() {
        manager.createTask(new Task("Task1", "Description1", Status.NEW));
        Epic epic = new Epic("Epic1", "Description Epic1");
        manager.createEpic(epic);
        manager.createSubtask(new Subtask("Subtask1", "Description Subtask1", epic.getId(), Status.NEW));

        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    @DisplayName("Загрузка и сохранение нескольких задач")
    void saveAndLoadMultipleTasks() {
        Task task1 = new Task("Task1", "Description1", Status.NEW);
        manager.createTask(task1);
        Task task2 = new Task("Task2", "Description2", Status.IN_PROGRESS);
        manager.createTask(task2);
        Epic epic = new Epic("Epic1", "Description Epic1");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1", epic.getId(), Status.NEW);
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2", epic.getId(), Status.DONE);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(2, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(2, manager.getAllSubtasks().size());
    }

}