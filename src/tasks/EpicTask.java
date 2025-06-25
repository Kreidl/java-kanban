package tasks;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class EpicTask extends Task {
    private LocalDateTime endTime;

    ArrayList<Subtask> subtasks = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, description);
        this.setTaskStatus(TaskStatus.NEW);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        try {
            if (getStartTime() != null && getDuration() != null) {
                endTime = getStartTime().plus(getDuration());
            } else {
                throw new NullPointerException("Время начала или продолжительность не могут быть пустыми");
            }
        } catch (NullPointerException exc) {
            exc.getMessage();
        }
        return endTime;
    }

    public void setDuration() {
        Duration duration = null;
        setStartTime();
        setEndTime();
        if (this.getEndTime() != null && this.getStartTime() != null) {
            duration = Duration.between(this.getStartTime(), this.getEndTime());
        }
        super.setDuration(duration);
    }

    public void setStartTime() {
        if (!subtasks.isEmpty()) {
            Optional<LocalDateTime> startTime = subtasks.stream()
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder());
            super.setStartTime(startTime.get());
        } else {
            super.setStartTime(null);
        }
    }

    public void setEndTime() {
        if (!subtasks.isEmpty()) {
            Optional<LocalDateTime> endTime = subtasks.stream()
                    .map(Task::getEndTime)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder());
            this.endTime = endTime.get();
        } else {
            this.endTime = null;
        }
    }
}
