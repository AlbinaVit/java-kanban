package service;

import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        taskManager = new InMemoryTaskManager();
        return taskManager;
    }

    @Test
    @DisplayName("Тестирование приоритизированных задач")
    void testGetPrioritizedTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 10, 0));
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        Task task3 = new Task("Task 3", "Description 3", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 11, 0));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritizedTasks.size(), "Должно быть 3 приоритизированные задачи");
        assertEquals(task2, prioritizedTasks.get(0), "Первая задача должна быть Task 2");
        assertEquals(task1, prioritizedTasks.get(1), "Вторая задача должна быть Task 1");
        assertEquals(task3, prioritizedTasks.get(2), "Третья задача должна быть Task 3");
    }
}