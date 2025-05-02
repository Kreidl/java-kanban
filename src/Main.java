import Manager.InMemoryTaskManager;
import Tasks.EpicTask;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        // Создание новых обычных задач
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        System.out.println("\nСписок обычных задач:");
        System.out.println(inMemoryTaskManager.getAllTasks().toString());

        //Получить задачу по id
        System.out.println("\nПолучаем задачу по ID:");
        System.out.println(inMemoryTaskManager.getTaskById(task2.getTaskId()).toString());

        // Обновление задачи
        Task task3 = new Task("Обновлённая задача 2", "Обновлённое описание задачи 2", TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task2, task3);
        System.out.println("\nСписок обновлённых задач:");
        System.out.println(inMemoryTaskManager.getAllTasks().toString());

        //Создание новых эпиков
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание эпика 2");
        inMemoryTaskManager.addEpicTask(epicTask1);
        inMemoryTaskManager.addEpicTask(epicTask2);
        System.out.println("\nСписок эпиков:");
        System.out.println(inMemoryTaskManager.getAllEpicTasks().toString());

        //Получить эпик по id
        System.out.println("\nПолучаем эпик по ID:");
        System.out.println(inMemoryTaskManager.getEpicTaskById(epicTask2.getTaskId()).toString());

        //Обновление эпика
        EpicTask epicTask3 = new EpicTask("Обновлённый эпик","Обновлённое описание эпика");
        inMemoryTaskManager.updateEpicTask(epicTask2, epicTask3);
        System.out.println("\nСписок обновлённых эпиков:");
        System.out.println(inMemoryTaskManager.getAllEpicTasks().toString());

        //Создание новых подзадач к эпикам
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", epicTask1.getTaskId());
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", epicTask2.getTaskId());
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.addSubtask(subtask3);
        System.out.println("\nСписок подзадач:");
        System.out.println(inMemoryTaskManager.getAllSubtasks().toString());

        //Получить подзадачу по id
        System.out.println("\nПолучаем подзадачу по ID:");
        System.out.println(inMemoryTaskManager.getSubtaskById(subtask1.getTaskId()).toString());

        //Обновление подзадач
        Subtask newSubtask1 = new Subtask("Обновлённая подзадача 1.1", "Описание обновлённой подзадачи 1.1",
                TaskStatus.DONE, epicTask1.getTaskId());
        Subtask newSubtask2 = new Subtask("Обновлённая подзадача 1.2", "Описание обновлённой подзадачи 1.2",
                TaskStatus.DONE, epicTask1.getTaskId());
        Subtask newSubtask3 = new Subtask("Обновлённая подзадача 2.1", "Описание обновлённой подзадачи 2.1",
                TaskStatus.IN_PROGRESS, epicTask1.getTaskId());
        inMemoryTaskManager.updateSubtask(subtask1, newSubtask1);
        inMemoryTaskManager.updateSubtask(subtask2, newSubtask2);
        inMemoryTaskManager.updateSubtask(subtask3, newSubtask3);
        System.out.println("\nСписок обновлённых подзадач:");
        System.out.println(inMemoryTaskManager.getAllSubtasks().toString());

        //Проверка статусов эпика в зависимости от статуса подзадач
        System.out.println("\nСтатус эпика 1: " + inMemoryTaskManager.getEpicTaskById(epicTask1.getTaskId()).getTaskStatus());
        System.out.println("\nСтатус эпика 2: " + inMemoryTaskManager.getEpicTaskById(epicTask2.getTaskId()).getTaskStatus());

        //Удаление задачи
        inMemoryTaskManager.deleteTaskById(task2);
        System.out.println("\nСписок задач после удаления одной задачи:");
        System.out.println(inMemoryTaskManager.getAllTasks().toString());

        //Удаление эпика
        inMemoryTaskManager.deleteEpicTaskById(epicTask2);
        System.out.println("\nСписок эпиков после удаления одного эпика:");
        System.out.println(inMemoryTaskManager.getAllEpicTasks().toString());

        //Удаление подзадачи
        inMemoryTaskManager.deleteSubtaskById(subtask2);
        System.out.println("\nСписок подзадач после удаления одной подзадачи:");
        System.out.println(inMemoryTaskManager.getAllSubtasks().toString());

        //Проверка, меняется ли статус эпика при удалении задач
        Subtask newSubtask4 = new Subtask("Обновлённая подзадача 1.2", "Описание обновлённой подзадачи 1.2",
                TaskStatus.IN_PROGRESS, epicTask1.getTaskId());
        inMemoryTaskManager.addSubtask(newSubtask4);
        System.out.println("\nНовый список подзадач:");
        System.out.println(inMemoryTaskManager.getAllSubtasks().toString());
        System.out.println("\nСтатус эпика 1: " + inMemoryTaskManager.getEpicTaskById(epicTask1.getTaskId()).getTaskStatus());
        inMemoryTaskManager.deleteSubtaskById(newSubtask4);
        System.out.println("\nУдалили подзадачу IN_PROGRESS");
        System.out.println(inMemoryTaskManager.getAllSubtasks().toString());
        System.out.println("\nСтатус эпика 1: " + inMemoryTaskManager.getEpicTaskById(epicTask1.getTaskId()).getTaskStatus());

        //Удаление всех задач
        inMemoryTaskManager.deleteAllTasks();
        System.out.println("\nСписок обычных задач (после удаления всех задач:");
        System.out.println(inMemoryTaskManager.getAllTasks().toString());

        //Удаление всех эпиков
        inMemoryTaskManager.deleteAllEpicTasks();
        System.out.println("\nСписок эпиков (после удаления всех эпиков:");
        System.out.println(inMemoryTaskManager.getAllEpicTasks().toString());

        //Удаление всех задач
        inMemoryTaskManager.deleteAllSubtasks();
        System.out.println("\nСписок подзадач (после удаления всех подзадач:");
        System.out.println(inMemoryTaskManager.getAllSubtasks().toString());
    }
}

