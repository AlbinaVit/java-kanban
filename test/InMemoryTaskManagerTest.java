import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }


    @Test
    void testCreateTask() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        Task createdTask = taskManager.createTask(task);

        assertNotNull(createdTask);
        assertEquals(1, createdTask.getId());
        assertEquals("Task 1", createdTask.getName());
        assertEquals("Description 1", createdTask.getDescription());
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId(), Status.NEW);
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        assertNotNull(createdSubtask);
        assertEquals(2, createdSubtask.getId());
        assertEquals("Subtask 1", createdSubtask.getName());
        assertEquals(epic.getId(), createdSubtask.getEpicId());
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        Epic createdEpic = taskManager.createEpic(epic);

        assertNotNull(createdEpic);
        assertEquals(1, createdEpic.getId());
        assertEquals("Epic 1", createdEpic.getName());
    }

    @Test
    void testGetAllTasks() {
        taskManager.createTask(new Task("Task 1", "Description 1", Status.NEW));
        taskManager.createTask(new Task("Task 2", "Description 2", Status.NEW));

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void testGetTaskById() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(1);
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Task 1", "Description 1", Status.IN_PROGRESS);
        taskManager.createTask(task);

        task.setName("Updated Task 1");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task 1", updatedTask.getName());
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Task 1", "Description 1", Status.DONE);
        taskManager.createTask(task);

        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testDeleteEpicAndSubtasks() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId(), Status.DONE);
        taskManager.createSubtask(subtask);

        taskManager.deleteEpic(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1.getId(), history.get(0).getId());
    }
}