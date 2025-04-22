package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Status;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private Epic epic;

    @BeforeEach
    public void setUp() {
        epic = new Epic("Epic 1", "Description");
    }

    @Test
    void testEpicStatusAllNew() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId(), Status.NEW, Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId(), Status.NEW, Duration.ofHours(1), LocalDateTime.now());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void testEpicStatusAllDone() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId(), Status.DONE, Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId(), Status.DONE, Duration.ofHours(1), LocalDateTime.now());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void testEpicStatusMixed() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId(), Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId(), Status.DONE, Duration.ofHours(1), LocalDateTime.now());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testEpicStatusInProgress() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId(), Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.now());
        epic.addSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

}