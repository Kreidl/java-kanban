package manager;

import exceptions.TaskIntersectWithOther;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    private int count = 0;

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, EpicTask> epicTasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected TreeSet<Task> prioritizedTasks;

    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>((t1, t2) -> {
            if (t1.getStartTime().isBefore(t2.getStartTime())) {
                return 1;
            } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return -1;
            }
            return 0;
        });
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (task.getStartTime() != null) {
            task.setEndTime();
            if (!isTaskIntersectWithOthers(task)) {
                count++;
                task.setTaskId(count);
                tasks.put(count, task);
                prioritizedTasks.add(task);
            }
        } else if (task.getStartTime() == null) {
            count++;
            task.setTaskId(count);
            tasks.put(count, task);
        }
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
        if (subtask.getStartTime() != null) {
            subtask.setEndTime();
            if (!isTaskIntersectWithOthers(subtask)) {
                subtask.setEpicId(subtask.getTaskId());
                count++;
                subtask.setTaskId(count);
                subtasks.put(subtask.getTaskId(),subtask);
                epicTasks.get(subtask.getEpicId()).getSubtasks().add(subtask);
                updateEpicTaskStatus(epicTasks.get(subtask.getEpicId()));
                prioritizedTasks.add(subtask);
            }
        } else if (subtask.getStartTime() == null) {
            subtask.setEpicId(subtask.getTaskId());
            count++;
            subtask.setTaskId(count);
            subtasks.put(subtask.getTaskId(),subtask);
            epicTasks.get(subtask.getEpicId()).getSubtasks().add(subtask);
            updateEpicTaskStatus(epicTasks.get(subtask.getEpicId()));
        }
    }

    @Override
    public void updateTask(Task thisTask, Task updatedTask) {
        if (tasks.get(thisTask.getTaskId()).equals(thisTask)) {
            if (prioritizedTasks.contains(thisTask)) {
                prioritizedTasks.remove(thisTask);
            }
            if (updatedTask.getStartTime() != null) {
                if (!isTaskIntersectWithOthers(updatedTask)) {
                    thisTask.setName(updatedTask.getName());
                    thisTask.setDescription(updatedTask.getDescription());
                    thisTask.setTaskStatus(updatedTask.getTaskStatus());
                    thisTask.setStartTime(updatedTask.getStartTime());
                    thisTask.setDuration(updatedTask.getDuration());
                    thisTask.setEndTime();
                    tasks.put(thisTask.getTaskId(), thisTask);
                }
                prioritizedTasks.add(thisTask);
            } else {
                thisTask.setName(updatedTask.getName());
                thisTask.setDescription(updatedTask.getDescription());
                thisTask.setTaskStatus(updatedTask.getTaskStatus());
                tasks.put(thisTask.getTaskId(), thisTask);
            }
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
            if (prioritizedTasks.contains(thisSubtask)) {
                prioritizedTasks.remove(thisSubtask);
            }
            if (updatedSubtask.getStartTime() != null) {
                if (!isTaskIntersectWithOthers(updatedSubtask)) {
                    thisSubtask.setName(updatedSubtask.getName());
                    thisSubtask.setDescription(updatedSubtask.getDescription());
                    thisSubtask.setTaskStatus(updatedSubtask.getTaskStatus());
                    thisSubtask.setStartTime(updatedSubtask.getStartTime());
                    thisSubtask.setDuration(updatedSubtask.getDuration());
                    thisSubtask.setEndTime();
                    subtasks.put(thisSubtask.getTaskId(), thisSubtask);
                    int indexSubtask = epicTasks.get(thisSubtask.getEpicId()).getSubtasks().indexOf(thisSubtask);
                    epicTasks.get(thisSubtask.getEpicId()).getSubtasks().set(indexSubtask, thisSubtask);
                }
                prioritizedTasks.add(thisSubtask);
            } else {
                thisSubtask.setName(updatedSubtask.getName());
                thisSubtask.setDescription(updatedSubtask.getDescription());
                thisSubtask.setTaskStatus(updatedSubtask.getTaskStatus());
                subtasks.put(thisSubtask.getTaskId(), thisSubtask);
                int indexSubtask = epicTasks.get(thisSubtask.getEpicId()).getSubtasks().indexOf(thisSubtask);
                epicTasks.get(thisSubtask.getEpicId()).getSubtasks().set(indexSubtask, thisSubtask);
            }
            updateEpicTaskStatus(epicTasks.get(thisSubtask.getEpicId()));
        }
    }

    @Override
    public void deleteTaskById(Task task) {
        if (inMemoryHistoryManager.getHistory().contains(task)) {
            inMemoryHistoryManager.remove(task.getTaskId());
        }
        tasks.remove(task.getTaskId());
        if (prioritizedTasks.contains(task)) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void deleteEpicTaskById(EpicTask epicTask) {
        inMemoryHistoryManager.remove(epicTask.getTaskId());
        for (Subtask subtask : epicTask.getSubtasks()) {
            subtasks.remove(subtask.getTaskId());
            if (inMemoryHistoryManager.getHistory().contains(subtask)) {
                inMemoryHistoryManager.remove(subtask.getTaskId());
            }
            if (prioritizedTasks.contains(subtask)) {
                prioritizedTasks.remove(subtask);
            }
        }
        epicTasks.remove(epicTask.getTaskId());
    }

    @Override
    public void deleteSubtaskById(Subtask subtask) {
        if (inMemoryHistoryManager.getHistory().contains(subtask)) {
            inMemoryHistoryManager.remove(subtask.getTaskId());
        }
        if (prioritizedTasks.contains(subtask)) {
            prioritizedTasks.remove(subtask);
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
            if (prioritizedTasks.contains(task)) {
                prioritizedTasks.remove(task);
            }
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpicTasks() {
        for (EpicTask epictask : epicTasks.values()) {
            inMemoryHistoryManager.remove(epictask.getTaskId());
            for (Subtask subtask : epictask.getSubtasks()) {
                if (prioritizedTasks.contains(subtask)) {
                    prioritizedTasks.remove(subtask);
                }
            }
        }
        epicTasks.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Task subtask : subtasks.values()) {
            inMemoryHistoryManager.remove(subtask.getTaskId());
            if (prioritizedTasks.contains(subtask)) {
                prioritizedTasks.remove(subtask);
            }
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

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public boolean isTwoTasksIntersect(Task task1, Task task2) {
        return (task1.getStartTime().isEqual(task2.getStartTime()) && task1.getEndTime().equals(task2.getEndTime())) ||
                (task1.getEndTime().isAfter(task2.getStartTime()) && task1.getStartTime().isBefore(task2.getEndTime()));
    }

    public boolean isTaskIntersectWithOthers(Task task) {
        Task taskBefore =  prioritizedTasks.ceiling(task);
        Task taskAfter = prioritizedTasks.floor(task);
        if (task.getStartTime() == null || ((taskBefore == null) && taskAfter == null)) {
            return false;
        } else {
            if (taskBefore != null) {
                if (isTwoTasksIntersect(task, taskBefore)) {
                    throw new TaskIntersectWithOther("Задача пересекается с другой");
                }
            }
            if (taskAfter != null) {
                if (isTwoTasksIntersect(task, taskAfter)) {
                    throw new TaskIntersectWithOther("Задача пересекается с другой");
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) inMemoryHistoryManager.getHistory();
    }

}

