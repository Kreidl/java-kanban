package Manager;

import Tasks.EpicTask;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int count = 0;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subtasks = new HashMap<>();

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
        if (subtasks.isEmpty()) {
            subtasks.put(subtask.getEpicId(), new ArrayList<>());
            subtasks.get(subtask.getEpicId()).add(subtask);
        } else {
            for (int id : subtasks.keySet()) {
                if (id == subtask.getEpicId()) {
                    subtasks.get(id).add(subtask);
                } else {
                    subtasks.put(subtask.getEpicId(), new ArrayList<>());
                    subtasks.get(subtask.getEpicId()).add(subtask);
                }
            }
        }
        for (EpicTask epicTask : epicTasks.values()) {
            if (epicTask.getTaskId() == subtask.getTaskId()) {
                epicTask.addNewSubtask(subtask);
            }
        }
    }

    public void updateTask(Task thisTask, Task updatedTask) {
        for (Task task : tasks.values()) {
            if (task.equals(thisTask)) {
                task.setName(updatedTask.getName());
                task.setDescription(updatedTask.getDescription());
                task.setTaskStatus(updatedTask.getTaskStatus());
                tasks.put(task.getTaskId(), task);
            }
        }
    }

    public void updateEpicTask(EpicTask thisEpicTask, EpicTask updatedEpicTask) {
        for (EpicTask epicTask : epicTasks.values()) {
            if (epicTask.equals(thisEpicTask)) {
                epicTask.setName(updatedEpicTask.getName());
                epicTask.setDescription(updatedEpicTask.getDescription());
                epicTask.setTaskStatus(updatedEpicTask.getTaskStatus());
                epicTasks.put(epicTask.getTaskId(), epicTask);
            }
        }
    }

    public void updateSubtask(Subtask thisSubtask, Subtask updatedSubtask) {
        for (int id : subtasks.keySet()) {
            if (id == thisSubtask.getEpicId()) {
                for (Subtask subtask : subtasks.get(id)) {
                    if (subtask.equals(thisSubtask)) {
                        subtask.setName(updatedSubtask.getName());
                        subtask.setDescription(updatedSubtask.getDescription());
                        subtask.setTaskStatus(updatedSubtask.getTaskStatus());
                        int indexSubtask = subtasks.get(id).indexOf(subtask);
                        subtasks.get(id).set(indexSubtask, subtask);
                    }
                }
                for (EpicTask epicTask : epicTasks.values()) {
                    if (epicTask.getTaskId() == thisSubtask.getTaskId()) {
                        epicTask.updateSubtask(thisSubtask, updatedSubtask);
                        if (isEpicTaskDone(epicTask)) {
                            epicTask.setTaskStatus(TaskStatus.DONE);
                        }
                        if (isEpicTaskInProgress(epicTask)) {
                            epicTask.setTaskStatus(TaskStatus.IN_PROGRESS);
                        }
                    }
                }
            }
        }
    }

    public void deleteTaskById(Task task) {
        for (Task task1 : tasks.values()) {
            if (task1.equals(task)) {
                tasks.remove(task1.getTaskId());
            }
        }
    }

    public void deleteEpicTaskById(EpicTask epicTask) {
        for (EpicTask epicTask1 : epicTasks.values()) {
            if (epicTask1.equals(epicTask)) {
                if (epicTask1.getSubtasks().equals(epicTask.getSubtasks())) {
                    epicTasks.remove(epicTask1.getTaskId(), epicTask1);
                    subtasks.remove(epicTask1.getTaskId());
                }
            }
        }
    }

    public void deleteSubtaskById(Subtask subtask) {
        subtasks.get(subtask.getEpicId()).remove(subtask);
        epicTasks.get(subtask.getEpicId()).removeSubtask(subtask);
            if (isEpicTaskDone(epicTasks.get(subtask.getEpicId()))) {
                epicTasks.get(subtask.getEpicId()).setTaskStatus(TaskStatus.DONE);
            }
            if (isEpicTaskInProgress(epicTasks.get(subtask.getEpicId()))) {
                epicTasks.get(subtask.getEpicId()).setTaskStatus(TaskStatus.IN_PROGRESS);
            }
        }

    public void getTaskById(int taskId) {
        System.out.println(tasks.get(taskId).toString());
    }

    public void getEpicTaskById(int taskId) {
        System.out.println(epicTasks.get(taskId).toString());
    }

    public void getSubtaskById(int taskId) {
        System.out.println(subtasks.get(taskId).toString());
    }

    public void printAllTasks() {
        for (Task task : tasks.values()) {
                System.out.println(task.toString());
            }
        }

    public void printAllEpicTasks() {
        for (EpicTask epicTask : epicTasks.values()) {
            System.out.println(epicTask.toString());
        }
    }

    public void printAllSubtasks() {
        for (ArrayList<Subtask> subtaskList : subtasks.values()) {
            System.out.println(subtaskList);
        }
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

    public EpicTask getEpicTasks(int taskId) {
        return epicTasks.get(taskId);
    }
}

