package manager;

import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int count = 0;

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, Subtask> subtasks;

    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        count++;
        task.setTaskId(count);
        tasks.put(count, task);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        if (epicTask == null) {
            return;
        }
        count++;
        epicTask.setTaskId(count);
        epicTasks.put(count, epicTask);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        subtask.setEpicId(subtask.getTaskId());
        count++;
        subtask.setTaskId(count);
        subtasks.put(subtask.getTaskId(),subtask);
        epicTasks.get(subtask.getEpicId()).getSubtasks().add(subtask);
        updateEpicTaskStatus(epicTasks.get(subtask.getEpicId()));
    }

    @Override
    public void updateTask(Task thisTask, Task updatedTask) {
        if (tasks.get(thisTask.getTaskId()).equals(thisTask)) {
            thisTask.setName(updatedTask.getName());
            thisTask.setDescription(updatedTask.getDescription());
            thisTask.setTaskStatus(updatedTask.getTaskStatus());
            tasks.put(thisTask.getTaskId(), thisTask);
        }
    }

    @Override
    public void updateEpicTask(EpicTask thisEpicTask, EpicTask updatedEpicTask) {
        if (epicTasks.get(thisEpicTask.getTaskId()).equals(thisEpicTask)) {
            thisEpicTask.setName(updatedEpicTask.getName());
            thisEpicTask.setDescription(updatedEpicTask.getDescription());
            thisEpicTask.setTaskStatus(updatedEpicTask.getTaskStatus());
            epicTasks.put(thisEpicTask.getTaskId(), thisEpicTask);
        }
    }

    @Override
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

    @Override
    public void deleteTaskById(Task task) {
        if (inMemoryHistoryManager.getHistory().contains(task)) {
            inMemoryHistoryManager.remove(task.getTaskId());
        }
        tasks.remove(task.getTaskId());
    }

    @Override
    public void deleteEpicTaskById(EpicTask epicTask) {
        inMemoryHistoryManager.remove(epicTask.getTaskId());
        for (Subtask subtask : epicTask.getSubtasks()) {
            subtasks.remove(subtask.getTaskId());
            if (inMemoryHistoryManager.getHistory().contains(subtask)) {
                inMemoryHistoryManager.remove(subtask.getTaskId());
            }
        }
        epicTasks.remove(epicTask.getTaskId());
    }

    @Override
    public void deleteSubtaskById(Subtask subtask) {
        if (inMemoryHistoryManager.getHistory().contains(subtask)) {
            inMemoryHistoryManager.remove(subtask.getTaskId());
        }
        epicTasks.get(subtask.getEpicId()).removeSubtask(subtask);
        subtasks.remove(subtask.getTaskId());
        updateEpicTaskStatus(epicTasks.get(subtask.getEpicId()));
    }

    @Override
    public Task getTaskById(int taskId) {
        inMemoryHistoryManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public EpicTask getEpicTaskById(int taskId) {
        inMemoryHistoryManager.add(epicTasks.get(taskId));
        return epicTasks.get(taskId);
    }

    @Override
    public Subtask getSubtaskById(int taskId) {
        inMemoryHistoryManager.add(subtasks.get(taskId));
        return subtasks.get(taskId);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        for (Task task : tasks.values()) {
            inMemoryHistoryManager.add(tasks.get(task.getTaskId()));
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<EpicTask> getAllEpicTasks() {
        for (EpicTask epicTask : epicTasks.values()) {
            inMemoryHistoryManager.add(epicTasks.get(epicTask.getTaskId()));
        }
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            inMemoryHistoryManager.add(subtasks.get(subtask.getTaskId()));
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            inMemoryHistoryManager.remove(task.getTaskId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpicTasks() {
        for (Task epictask : epicTasks.values()) {
            inMemoryHistoryManager.remove(epictask.getTaskId());
        }
        epicTasks.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Task subtask : subtasks.values()) {
            inMemoryHistoryManager.remove(subtask.getTaskId());
        }
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.getSubtasks().clear();
            updateEpicTaskStatus(epicTask);
        }
        subtasks.clear();
    }

    public boolean isEpicTaskDone(EpicTask epicTask) {
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

    public boolean isEpicTaskInProgress(EpicTask epicTask) {
        boolean isItInProgress = false;
        for (Subtask subtask : epicTask.getSubtasks()) {
            if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                isItInProgress = true;
                break;
            }
        }
        return isItInProgress;
    }

    public boolean isEpicTaskNew(EpicTask epicTask) {
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

    public void updateEpicTaskStatus(EpicTask epicTask) {
        if (isEpicTaskNew(epicTask)) {
            epicTask.setTaskStatus(TaskStatus.NEW);
        } else if (isEpicTaskInProgress(epicTask)) {
            epicTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (isEpicTaskDone(epicTask)) {
            epicTask.setTaskStatus(TaskStatus.DONE);
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        if (epicTask == null) {
            return null;
        }
        for (Subtask subtask : epicTask.getSubtasks()) {
            inMemoryHistoryManager.add(subtasks.get(subtask.getTaskId()));
        }
        return epicTask.getSubtasks();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) inMemoryHistoryManager.getHistory();
    }

}

