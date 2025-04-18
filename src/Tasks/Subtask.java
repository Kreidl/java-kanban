package Tasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description,  int taskId) {
        super(name, description, taskId);
        this.setTaskStatus(TaskStatus.NEW);
        this.epicId = taskId;
    }

    public Subtask(String name, String description, TaskStatus taskStatus, int taskId) {
        super(name, description, taskStatus);
        this.epicId = taskId;
    }

    public int getEpicId() {
        return epicId;
    }


}
