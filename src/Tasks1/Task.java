package Tasks1;

import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private int taskId;
    private TaskStatus taskStatus;

    public Task(String name, String description, int taskId) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
    }

    public Task(String name, String description, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        taskStatus = TaskStatus.NEW;
    }

    public Task(String name, String description, TaskStatus taskStatus, int taskId) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        this.taskStatus = taskStatus;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return  Objects.equals(name, task.name) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, taskId, taskStatus);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskId=" + taskId +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
