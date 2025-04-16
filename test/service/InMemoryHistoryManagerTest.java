package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("Добавление задачи в историю")
    void addTask_AddsTaskToHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        task1.setId(1);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    @DisplayName("Добавление нескольких задач в историю")
    void addTask_AddsMultipleTasksToHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 11, 0));
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    @DisplayName("Добавление задачи в историю и удаление дубликата")
    void addTask_AddsTaskToHistory_AndRemovesDuplicate() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        task1.setId(1);

        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    @DisplayName("Удаление дубликата")
    void removeTask_RemovesTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 3));
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    @DisplayName("Возврат пустого списка, где нет задач")
    void getHistory_ReturnsEmptyList_WhenNoTasks() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    @DisplayName("Добавление пустой задачи")
    void addTask_SupportsNullTask() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    @DisplayName("Удаление несуществующей задачи")
    void addTask_HandlesRemovingNonExistentTask() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 0));
        task1.setId(1);

        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    @DisplayName("Удаление задачи из начала истории")
    void removeTask_RemovesTaskFromBeginning() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 1));
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 2));
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 02, 1, 9, 3));
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    @DisplayName("Удаление задачи с середины истории")
    void removeTask_RemovesFromMiddle() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 2, 1, 9, 0));
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 2, 2, 9, 0));
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        assertTrue(historyManager.getHistory().size() == 1);
        assertTrue(historyManager.getHistory().contains(task2));
    }

    @Test
    @DisplayName("Удаление задачи с конца истории")
    void removeTask_RemovesFromEnd() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 2, 1, 9, 0));
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 2, 2, 9, 0));
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        assertTrue(historyManager.getHistory().size() == 1);
        assertTrue(historyManager.getHistory().contains(task1));
    }
}