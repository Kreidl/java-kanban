package tasks;

import manager.TaskType;

import java.util.ArrayList;

public class EpicTask extends Task {
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
}
