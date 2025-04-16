package model;

import utils.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, Duration.ZERO, null);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateEpicDetails();
    }

    public void updateEpicDetails() {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask subtask : subtasks) {
            totalDuration = totalDuration.plus(subtask.getDuration());
            if (earliestStartTime == null || (subtask.getStartTime() != null && subtask.getStartTime().isBefore(earliestStartTime))) {
                earliestStartTime = subtask.getStartTime();
            }
            if (latestEndTime == null || (subtask.getEndTime() != null && subtask.getEndTime().isAfter(latestEndTime))) {
                latestEndTime = subtask.getEndTime();
            }

            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
        }

        this.duration = totalDuration;
        this.startTime = earliestStartTime;
        this.endTime = latestEndTime;

        if (allDone) {
            this.status = Status.DONE;
        } else if (anyInProgress) {
            this.status = Status.IN_PROGRESS;
        } else {
            this.status = Status.NEW;
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public boolean removeSubtask(Subtask subtask) {
        boolean removed = subtasks.remove(subtask);
        if (removed) {
            updateEpicDetails();
        }
        return removed;
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

}
