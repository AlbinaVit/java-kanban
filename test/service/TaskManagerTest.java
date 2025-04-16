package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager() throws Exception;

    @BeforeEach
    public void setUp() throws Exception {
        taskManager = createTaskManager();
    }

    @Test
    void testCreateTask() {
        Task task = new Task("model.Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        Task createdTask = taskManager.createTask(task);

        assertNotNull(createdTask);
        assertEquals(1, createdTask.getId());
        assertEquals("model.Task 1", createdTask.getName());
        assertEquals("Description 1", createdTask.getDescription());
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("model.Epic 1", "model.Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("model.Subtask 1", "model.Subtask Description", epic.getId(), Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        assertNotNull(createdSubtask);
        assertEquals(2, createdSubtask.getId());
        assertEquals("model.Subtask 1", createdSubtask.getName());
        assertEquals(epic.getId(), createdSubtask.getEpicId());
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic("model.Epic 1", "model.Epic Description");
        Epic createdEpic = taskManager.createEpic(epic);

        assertNotNull(createdEpic);
        assertEquals(1, createdEpic.getId());
        assertEquals("model.Epic 1", createdEpic.getName());
    }

    @Test
    void testGetAllTasks() {
        taskManager.createTask(new Task("model.Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0)));
        taskManager.createTask(new Task("model.Task 2", "Description 2", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 5)));

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void testGetTaskById() {
        Task task = new Task("model.Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(1);
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("model.Task 1", "Description 1", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        taskManager.createTask(task);

        task.setName("Updated model.Task 1");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated model.Task 1", updatedTask.getName());
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("model.Task 1", "Description 1", Status.DONE, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        taskManager.createTask(task);

        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testDeleteEpicAndSubtasks() {
        Epic epic = new Epic("model.Epic 1", "model.Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("model.Subtask 1", "model.Subtask Description", epic.getId(), Status.DONE, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        taskManager.createSubtask(subtask);

        taskManager.deleteEpic(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task("model.Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        Task task2 = new Task("model.Task 2", "Description 2", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 10, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1.getId(), history.get(0).getId());
    }
}