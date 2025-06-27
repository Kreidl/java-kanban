package tasks;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class EpicTask extends Task {
    protected LocalDateTime endTime;

    private ArrayList<Subtask> subtasks = new ArrayList<>();

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
        return endTime;
    }

    public void setDuration() {
        Optional<Duration> duration = subtasks.stream()
                .filter(Objects::nonNull)
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus);
        super.setDuration(duration.get());
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
            if (endTime.isPresent()) {
                this.endTime = endTime.get();
            } else {
                this.endTime = null;
            }
        } else {
            this.endTime = null;
        }
    }
}
