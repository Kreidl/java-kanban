import Manager.TaskManager;
import Tasks.EpicTask;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        // Создание новых обычных задач
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        System.out.println("\nСписок обычных задач:");
        taskManager.printAllTasks();

        //Получить задачу по id
        System.out.println("\nПолучаем задачу по ID:");
        taskManager.getTaskById(task2.getTaskId());

        // Обновление задачи
        Task task3 = new Task("Обновлённая задача 2", "Обновлённое описание задачи 2", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2, task3);
        System.out.println("\nСписок обновлённых задач:");
        taskManager.printAllTasks();

        //Создание новых эпиков
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание эпика 2");
        taskManager.addEpicTask(epicTask1);
        taskManager.addEpicTask(epicTask2);
        System.out.println("\nСписок эпиков:");
        taskManager.printAllEpicTasks();

        //Получить эпик по id
        System.out.println("\nПолучаем эпик по ID:");
        taskManager.getEpicTaskById(epicTask2.getTaskId());

        //Обновление эпика
        EpicTask epicTask3 = new EpicTask("Обновлённый эпик","Обновлённое описание эпика", TaskStatus.IN_PROGRESS);
        taskManager.updateEpicTask(epicTask2, epicTask3);
        System.out.println("\nСписок обновлённых эпиков:");
        taskManager.printAllEpicTasks();

        //Создание новых подзадач к эпикам
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", epicTask1.getTaskId());
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", epicTask2.getTaskId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        System.out.println("\nСписок подзадач:");
        taskManager.printAllSubtasks();

        //Получить подзадачи по id
        System.out.println("\nПолучаем подзадачи по ID:");
        taskManager.getSubtaskById(epicTask1.getTaskId());

        //Обновление подзадач
        Subtask newSubtask1 = new Subtask("Обновлённая подзадача 1.1", "Описание обновлённой подзадачи 1.1",
                TaskStatus.DONE, epicTask1.getTaskId());
        Subtask newSubtask2 = new Subtask("Обновлённая подзадача 1.2", "Описание обновлённой подзадачи 1.2",
                TaskStatus.DONE, epicTask1.getTaskId());
        Subtask newSubtask3 = new Subtask("Обновлённая подзадача 2.1", "Описание обновлённой подзадачи 2.1",
                TaskStatus.IN_PROGRESS, epicTask1.getTaskId());
        taskManager.updateSubtask(subtask1, newSubtask1);
        taskManager.updateSubtask(subtask2, newSubtask2);
        taskManager.updateSubtask(subtask3, newSubtask3);
        System.out.println("\nСписок обновлённых подзадач:");
        taskManager.printAllSubtasks();

        //Проверка статусов эпика в зависимости от статуса подзадач
        System.out.println("\nСтатус эпика 1: " + taskManager.getEpicTasks(epicTask1.getTaskId()).getTaskStatus());
        System.out.println("\nСтатус эпика 2: " + taskManager.getEpicTasks(epicTask2.getTaskId()).getTaskStatus());

        //Удаление задачи
        taskManager.deleteTaskById(task2);
        System.out.println("\nСписок задач после удаления:");
        taskManager.printAllTasks();

        //Удаление эпика
        taskManager.deleteEpicTaskById(epicTask2);
        System.out.println("\nСписок эпиков после удаления:");
        taskManager.printAllEpicTasks();

        //Удаление подзадачи
        taskManager.deleteSubtaskById(subtask2);
        System.out.println("\nСписок подзадач после удаления:");
        taskManager.printAllSubtasks();

        //Проверка, меняется ли статус эпика при удалении задач
        taskManager.addSubtask(subtask2);
        Subtask newSubtask4 = new Subtask("Обновлённая подзадача 1.2", "Описание обновлённой подзадачи 1.2",
                TaskStatus.IN_PROGRESS, epicTask1.getTaskId());
        taskManager.updateSubtask(subtask2, newSubtask4);
        System.out.println("\nНовый список подзадач:");
        taskManager.printAllSubtasks();
        System.out.println("\nСтатус эпика 1: " + taskManager.getEpicTasks(epicTask1.getTaskId()).getTaskStatus());
        taskManager.deleteSubtaskById(newSubtask4);
        System.out.println("\nУдалили подзадачу IN_PROGRESS");
        taskManager.printAllSubtasks();
        System.out.println("\nСтатус эпика 1: " + taskManager.getEpicTasks(epicTask1.getTaskId()).getTaskStatus());
    }
}

