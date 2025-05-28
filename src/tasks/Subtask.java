package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description,  int taskId) {
        super(name, description, taskId);
        this.setTaskStatus(TaskStatus.NEW);
    }

    public Subtask(String name, String description, TaskStatus taskStatus, int taskId) {
        super(name, description, taskStatus, taskId);
        this.epicId = taskId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
