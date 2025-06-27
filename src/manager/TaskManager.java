package manager;

import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    void addTask(Task task);

    void addEpicTask(EpicTask epicTask);

    void addSubtask(Subtask subtask);

    void updateTask(Task thisTask, Task updatedTask);

    void updateEpicTask(EpicTask thisEpicTask, EpicTask updatedEpicTask);

    void updateSubtask(Subtask thisSubtask, Subtask updatedSubtask);

    void deleteTaskById(Task task);

    void deleteEpicTaskById(EpicTask epicTask);

    void deleteSubtaskById(Subtask subtask);

    Task getTaskById(int taskId);

    EpicTask getEpicTaskById(int taskId);

    Subtask getSubtaskById(int taskId);

    ArrayList<Task> getAllTasks();

    ArrayList<EpicTask> getAllEpicTasks();

    ArrayList<Subtask> getAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubtasks();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    ArrayList<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();
}
