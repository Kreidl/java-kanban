package manager;

import exceptions.ManagerSaveException;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileBacked;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTaskManager()  {
        super();
        fileBacked = FileBackedTaskManager.getFileBacked();
    }

    public FileBackedTaskManager(File savedTasks) {
        super();
        fileBacked = savedTasks;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        super.addEpicTask(epicTask);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task thisTask, Task updatedTask) {
        super.updateTask(thisTask, updatedTask);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask thisEpicTask, EpicTask updatedEpicTask) {
        super.updateEpicTask(thisEpicTask, updatedEpicTask);
        save();
    }

    @Override
    public void updateSubtask(Subtask thisSubtask, Subtask updatedSubtask) {
        super.updateSubtask(thisSubtask, updatedSubtask);
        save();
    }

    @Override
    public void deleteTaskById(Task task) {
        super.deleteTaskById(task);
        save();
    }

    @Override
    public void deleteEpicTaskById(EpicTask epicTask) {
        super.deleteEpicTaskById(epicTask);
        save();
    }

    @Override
    public void deleteSubtaskById(Subtask subtask) {
        super.deleteSubtaskById(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateEpicTaskStatus(EpicTask epicTask) {
        super.updateEpicTaskStatus(epicTask);
        save();
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileBacked, StandardCharsets.UTF_8))) {
            bw.write("\uFEFF");
            String firstString = "id,type,name,status,description,epic\n";
            bw.write(firstString);
            TreeMap<Integer, Task> allDifferentTasks = new TreeMap<>(tasks);
            allDifferentTasks.putAll(epicTasks);
            allDifferentTasks.putAll(subtasks);
            for (Task task : allDifferentTasks.values()) {
                bw.write(toString(task) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager;
        try {
            fileBackedTaskManager = new FileBackedTaskManager(file);
            String readFile = Files.readString(file.toPath());
            String[] tasks = readFile.split("\n");
            for (String s : tasks) {
                if (!s.equals("\uFEFF" + "id,type,name,status,description,epic") && !s.isEmpty()) {
                    Task task = fromString(s);
                    if (task.getType() == TaskType.EPIC) {
                        fileBackedTaskManager.addEpicTask((EpicTask) task);
                    } else if (task.getType() == TaskType.SUBTASK) {
                        fileBackedTaskManager.addSubtask((Subtask) task);
                    } else {
                        fileBackedTaskManager.addTask(task);
                    }
                }
            }
        } catch (NullPointerException exc) {
            throw new ManagerSaveException("Файл пустой.");
        } catch (SecurityException exc) {
            throw new ManagerSaveException("Недостаточно прав доступа.");
        } catch (Exception e) {
                throw new ManagerSaveException("Не получилось прочитать файл.");
            }
        return fileBackedTaskManager;
    }

    public static File getFileBacked() {
        File resources = new File(System.getProperty("user.dir"), "resources");
        if (!resources.exists() || !resources.isDirectory()) {
            resources.mkdir();
        }
        return new File("resources" + File.separator + "saveTasks.csv");
    }

    public String toString(Task task) {
        TaskType taskType = task.getType();
        String epicId = "";
        if  (taskType == TaskType.SUBTASK) epicId = String.valueOf(((Subtask) task).getEpicId());
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", task.getTaskId(), getTaskType(task),
                task.getName(), task.getDescription(), task.getTaskStatus(),
                task.getStartTime() != null ? task.getStartTime().format(formatter) : "",
                task.getEndTime() != null ? task.getEndTime().format(formatter) : "",
                task.getDuration() != null ? task.getDuration().toMinutes() : "",
                epicId);
    }

    public static Task fromString(String value) {
        String[] taskInfo = value.split(",", 9);
        switch (taskInfo[1]) {
            case "TASK":
                Task task = new Task(taskInfo[2], taskInfo[3], TaskStatus.valueOf(taskInfo[4]), Integer.parseInt(taskInfo[0]));
                setStartEndTimesAndDuration(task, taskInfo[5], taskInfo[6], taskInfo[7]);
                return task;
            case "EPIC":
                Task epicTask = new EpicTask(taskInfo[2], taskInfo[3]);
                epicTask.setTaskId(Integer.parseInt(taskInfo[0]));
                epicTask.setTaskStatus(TaskStatus.valueOf(taskInfo[4]));
                setStartEndTimesAndDuration(epicTask, taskInfo[5], taskInfo[6], taskInfo[7]);
                return epicTask;
            case "SUBTASK":
                Task subtask = new Subtask(taskInfo[2], taskInfo[3], Integer.parseInt(taskInfo[8]));
                subtask.setTaskStatus(TaskStatus.valueOf(taskInfo[4]));
                setStartEndTimesAndDuration(subtask, taskInfo[5], taskInfo[6], taskInfo[7]);
                return subtask;
        }
        return null;
    }

    public static void setStartEndTimesAndDuration(Task task, String start, String end, String duration) {
        if (!start.isEmpty()) {
            task.setStartTime(LocalDateTime.parse(start, formatter));
        } else {
            task.setStartTime(null);
        }
        if (!duration.isEmpty()) {
            task.setDuration(Duration.ofMinutes(Long.parseLong(duration)));
        } else {
            task.setStartTime(null);
        }
        if (!end.isEmpty()) {
            task.setEndTime();
        } else {
            task.setStartTime(null);
        }
    }

    private TaskType getTaskType(Task task) {
        if (task.getType() == TaskType.EPIC) {
            return TaskType.EPIC;
        } else if (task.getType() == TaskType.SUBTASK) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    public File getFile() {
        return fileBacked;
    }

    static class Main {
        public static void main(String[] args) {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
            Task task1 = new Task("Задача 1", "Описание задачи 1");
            fileBackedTaskManager.addTask(task1);
            EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
            fileBackedTaskManager.addEpicTask(epicTask1);
            Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask1.getTaskId());
            fileBackedTaskManager.addSubtask(subtask1);
            EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание эпика 2");
            fileBackedTaskManager.addEpicTask(epicTask2);
            Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", epicTask1.getTaskId());
            fileBackedTaskManager.addSubtask(subtask2);
            Task task2 = new Task("Задача 2", "Описание задачи 2");
            fileBackedTaskManager.addTask(task2);
            Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", epicTask2.getTaskId());
            fileBackedTaskManager.addSubtask(subtask3);
            FileBackedTaskManager fileBackedTaskManager1 = new FileBackedTaskManager(fileBackedTaskManager.getFile());
            try (BufferedReader br = new BufferedReader(new FileReader(fileBackedTaskManager1.getFile()))) {
                System.out.println("Проверяем наличие 7 добавленных в первый менеджер задач:\n");
                while (br.ready()) {
                    System.out.println(br.readLine());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}




