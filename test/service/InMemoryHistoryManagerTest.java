package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTask_AddsTaskToHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void addTask_AddsMultipleTasksToHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void addTask_AddsTaskToHistory_AndRemovesDuplicate() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);

        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void removeTask_RemovesTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void getHistory_ReturnsEmptyList_WhenNoTasks() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void addTask_SupportsNullTask() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void addTask_HandlesRemovingNonExistentTask() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);

        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }
}