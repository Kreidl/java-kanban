package Manager;

import Tasks.EpicTask;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager {
    private int count = 0;

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, Subtask> subtasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(Task task) {
        count++;
        task.setTaskId(count);
        tasks.put(count, task);
    }

    public void addEpicTask(EpicTask epicTask) {
        count++;
        epicTask.setTaskId(count);
        epicTasks.put(count, epicTask);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setEpicId(subtask.getTaskId());
        count++;
        subtask.setTaskId(count);
        subtasks.put(subtask.getTaskId(),subtask);
        epicTasks.get(subtask.getEpicId()).getSubtasks().add(subtask);
        updateEpicTaskStatus(epicTasks.get(subtask.getEpicId()));
    }

    public void updateTask(Task thisTask, Task updatedTask) {
        if (tasks.get(thisTask.getTaskId()).equals(thisTask)) {
            thisTask.setName(updatedTask.getName());
            thisTask.setDescription(updatedTask.getDescription());
            thisTask.setTaskStatus(updatedTask.getTaskStatus());
            tasks.put(thisTask.getTaskId(), thisTask);
        }
    }

    public void updateEpicTask(EpicTask thisEpicTask, EpicTask updatedEpicTask) {
        if (epicTasks.get(thisEpicTask.getTaskId()).equals(thisEpicTask)) {
            thisEpicTask.setName(updatedEpicTask.getName());
            thisEpicTask.setDescription(updatedEpicTask.getDescription());
            thisEpicTask.setTaskStatus(updatedEpicTask.getTaskStatus());
            epicTasks.put(thisEpicTask.getTaskId(), thisEpicTask);
        }
    }

    public void updateSubtask(Subtask thisSubtask, Subtask updatedSubtask) {
        if (subtasks.get(thisSubtask.getTaskId()).equals(thisSubtask)) {
            thisSubtask.setName(updatedSubtask.getName());
            thisSubtask.setDescription(updatedSubtask.getDescription());
            thisSubtask.setTaskStatus(updatedSubtask.getTaskStatus());
            subtasks.put(thisSubtask.getTaskId(), thisSubtask);
            int indexSubtask = epicTasks.get(thisSubtask.getEpicId()).getSubtasks().indexOf(thisSubtask);
            epicTasks.get(thisSubtask.getEpicId()).getSubtasks().set(indexSubtask, thisSubtask);
            updateEpicTaskStatus(epicTasks.get(thisSubtask.getEpicId()));
        }
    }

    public void deleteTaskById(Task task) {
        tasks.remove(task.getTaskId());
    }

    public void deleteEpicTaskById(EpicTask epicTask) {
        for (Subtask subtask : subtasks.values()) {
            if (epicTask.getTaskId() == subtask.getEpicId()) {
                subtasks.remove(subtask.getTaskId());
                }
            }
        epicTasks.remove(epicTask.getTaskId());
    }

    public void deleteSubtaskById(Subtask subtask) {
        epicTasks.get(subtask.getEpicId()).removeSubtask(subtask);
        subtasks.remove(subtask.getTaskId());
        updateEpicTaskStatus(epicTasks.get(subtask.getEpicId()));
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public EpicTask getEpicTaskById(int taskId) {
        return epicTasks.get(taskId);
    }

    public Subtask getSubtaskById(int taskId) {
        return subtasks.get(taskId);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllTasks () {
        tasks.clear();
    }

    public void deleteAllEpicTasks () {
        epicTasks.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks () {
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.getSubtasks().clear();
            updateEpicTaskStatus(epicTask);
        }
        subtasks.clear();;
    }

    public boolean isEpicTaskDone (EpicTask epicTask) {
        boolean isItDone = false;
        for (Subtask subtask : epicTask.getSubtasks()) {
            if (subtask.getTaskStatus() == TaskStatus.DONE) {
                isItDone = true;
            } else {
                isItDone = false;
                break;
            }
        }
        return isItDone;
    }

    public boolean isEpicTaskInProgress (EpicTask epicTask) {
        boolean isItInProgress = false;
        for (Subtask subtask : epicTask.getSubtasks()) {
            if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                isItInProgress = true;
                break;
            }
        }
        return isItInProgress;
    }

    public boolean isEpicTaskNew (EpicTask epicTask) {
        boolean isItNew = false;
        if (epicTask.getSubtasks().isEmpty()) {
            isItNew = true;
        } else {
            for (Subtask subtask : epicTask.getSubtasks()) {
                if (subtask.getTaskStatus() == TaskStatus.NEW) {
                    isItNew = true;
                } else {
                    isItNew = false;
                    break;
                }
            }
        }
        return isItNew;
    }

    public void updateEpicTaskStatus (EpicTask epicTask) {
        if (isEpicTaskNew(epicTask)) {
            epicTask.setTaskStatus(TaskStatus.NEW);
        } else if (isEpicTaskInProgress(epicTask)) {
            epicTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (isEpicTaskDone(epicTask)) {
            epicTask.setTaskStatus(TaskStatus.DONE);
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        if (epicTask == null) {
            return null;
        }
        return epicTask.getSubtasks();
    }
}

