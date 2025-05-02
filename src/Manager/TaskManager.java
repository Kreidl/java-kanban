package Manager;

import Tasks.EpicTask;
import Tasks.Subtask;
import Tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    public void addTask(Task task);

    public void addEpicTask(EpicTask epicTask);

    public void addSubtask(Subtask subtask);

    public void updateTask(Task thisTask, Task updatedTask);

    public void updateEpicTask(EpicTask thisEpicTask, EpicTask updatedEpicTask);

    public void updateSubtask(Subtask thisSubtask, Subtask updatedSubtask);

    public void deleteTaskById(Task task);

    public void deleteEpicTaskById(EpicTask epicTask);

    public void deleteSubtaskById(Subtask subtask);

    public Task getTaskById(int taskId);

    public EpicTask getEpicTaskById(int taskId);

    public Subtask getSubtaskById(int taskId);

    public ArrayList<Task> getAllTasks();

    public ArrayList<EpicTask> getAllEpicTasks();

    public ArrayList<Subtask> getAllSubtasks();

    public void deleteAllTasks();

    public void deleteAllEpicTasks();

    public void deleteAllSubtasks();

    public ArrayList<Subtask> getEpicSubtasks(int epicId);

    public ArrayList<Task> getHistory();
}
