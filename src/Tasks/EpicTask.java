package Tasks;

import java.util.ArrayList;

public class EpicTask extends Task {
    ArrayList<Subtask> subtasks = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, description);
        this.setTaskStatus(TaskStatus.NEW);
    }

    public EpicTask(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);
    }

    public void addNewSubtask (Subtask subtask) {
        subtasks.add(subtask);
    }

    public void updateSubtask (Subtask thisSubtask, Subtask updatedSubtask) {
        for (Subtask subtask : subtasks) {
            if (subtask.equals(thisSubtask)) {
                subtask.setName(updatedSubtask.getName());
                subtask.setDescription(updatedSubtask.getDescription());
                subtask.setTaskStatus(updatedSubtask.getTaskStatus());
                int indexSubtask = subtasks.indexOf(subtask);
                subtasks.set(indexSubtask, subtask);
            }
        }
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }
}
