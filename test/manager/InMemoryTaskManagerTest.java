package manager;

import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    static InMemoryTaskManager manager;
    static Task exampleTask;
    static EpicTask exampleEpicTask;
    static Subtask exampleSubtask;

    @BeforeAll
    static void createExamplesOfTasks() {
        exampleTask = new Task("Задача 100","Описание задачи 100");
        exampleEpicTask = new EpicTask("Эпик 100", "Описание эпика 100");
        exampleSubtask = new Subtask("Подзадача 100.1","Описание подзадачи 100.1", 2);
    }

    @BeforeEach
    void createNewInMemoryTaskManager() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addTaskTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = task1;
        manager.addTask(task1);
        assertEquals(exampleTask.getClass(), manager.getTaskById(task1.getTaskId()).getClass(), "Задача нужного класса не найдена");
        assertEquals(task2.getName(), task1.getName(), "Задача с заданным именем не добавлена");
        assertEquals(task2.getDescription(), task1.getDescription(), "Задача с заданным описанием не добавлена");
    }

    @Test
    void addEpicTaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        EpicTask epicTask2 = epicTask1;
        manager.addEpicTask(epicTask1);
        assertEquals(exampleEpicTask.getClass(), manager.getEpicTaskById(epicTask1.getTaskId()).getClass());
        assertEquals(epicTask2.getName(), epicTask1.getName(), "Эпик с заданным именем не добавлен");
        assertEquals(epicTask2.getDescription(), epicTask1.getDescription(), "Эпик с заданным описанием не добавлен");
    }

    @Test
    void addSubtaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = subtask1;
        manager.addSubtask(subtask1);
        assertEquals(exampleSubtask.getClass(), manager.getSubtaskById(subtask1.getTaskId()).getClass());
        assertEquals(subtask2.getName(), subtask1.getName(), "Подзадача с заданным именем не добавлена в менеджер задач");
        assertEquals(subtask2.getDescription(), subtask1.getDescription(), "Подзадача с заданным описанием не добавлена в менеджер задач");
        int indexSubtask = epicTask1.getSubtasks().indexOf(subtask1);
        assertEquals(exampleSubtask.getClass(), epicTask1.getSubtasks().get(indexSubtask).getClass());
        assertEquals(subtask2.getName(), epicTask1.getSubtasks().get(indexSubtask).getName(), "Подзадача с заданным именем не добавлена в эпик");
        assertEquals(subtask2.getDescription(), epicTask1.getSubtasks().get(indexSubtask).getDescription(),"Подзадача с заданным описанием не добавлена в эпик");
    }

    @Test
    void cannotAddVoidTask() {
        Task task = null;
        manager.addTask(task);
        ArrayList<Task> tasks= new ArrayList<>();
        assertEquals(tasks, manager.getAllTasks(), "Списки задач не совпадают");
    }

    @Test
    void cannotAddVoidEpicTask() {
        EpicTask epicTask = null;
        manager.addEpicTask(epicTask);
        ArrayList<EpicTask> epicTasks= new ArrayList<>();
        assertEquals(epicTasks, manager.getAllEpicTasks(), "Списки эпиков не совпадают");
    }

    @Test
    void cannotAddVoidSubtask() {
        Subtask subtask = null;
        manager.addSubtask(subtask);
        ArrayList<Subtask> subtasks= new ArrayList<>();
        assertEquals(subtasks, manager.getAllSubtasks(), "Списки подзадач не совпадают");
    }

    @Test
    void updateTaskTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        manager.addTask(task1);
        Task task2 = new Task("Обновлённое название задачи 1", "Обновлённое описание задачи 1", TaskStatus.IN_PROGRESS);
        manager.updateTask(task1, task2);
        assertEquals(task1, task2, "Задача не обновлена");
    }

    @Test
    void updateEpicTaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        EpicTask epicTask2 = new EpicTask("Название обновлённого эпика 1","Описание обновлённого эпика 1");
        manager.updateEpicTask(epicTask1, epicTask2);
        assertEquals(epicTask1, epicTask2, "Эпик не обновлен");
    }

    @Test
    void updateSubtaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Обновлённое название подзадачи 1.1","Обновлённое описание подзадачи 1.1", epicTask1.getTaskId());
        manager.updateSubtask(subtask1, subtask2);
        int indexSubtask = epicTask1.getSubtasks().indexOf(subtask1);
        assertEquals(subtask2, epicTask1.getSubtasks().get(indexSubtask), "Подзадача в эпике не обновлена");
        assertEquals(subtask2, subtask1, "Подзадача в менеджере задач не обновлена");
    }

    @Test
    void deleteTaskByIdTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        manager.addTask(task1);
        assertNotNull(manager.getTaskById(task1.getTaskId()));
        manager.deleteTaskById(manager.getTaskById(task1.getTaskId()));
        assertNull(manager.getTaskById(task1.getTaskId()), "Удаление задачи по id некорректно");
    }

    @Test
    void deleteEpicTaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        assertNotNull(manager.getEpicTaskById(epicTask1.getTaskId()));
        manager.deleteEpicTaskById(manager.getEpicTaskById(epicTask1.getTaskId()));
        assertNull(manager.getEpicTaskById(epicTask1.getTaskId()), "Удаление эпика по id некорректно");
    }

    @Test
    void deleteSubtaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        manager.addSubtask(subtask2);
        assertNotNull(manager.getSubtaskById(subtask1.getTaskId()), "Подзадача, добавленная в менеджер задач, не может быть пустой");
        assertNotNull(manager.getSubtaskById(subtask2.getTaskId()), "Подзадача, добавленная в менеджер задач, не может быть пустой");
        ArrayList<Subtask> subtasksTest = new ArrayList<>();
        subtasksTest.add(subtask1);
        manager.deleteSubtaskById(manager.getSubtaskById(subtask2.getTaskId()));
        assertEquals(subtasksTest, epicTask1.getSubtasks(), "Удаление подзадачи по id в эпике некорректно");
        assertEquals(subtasksTest, manager.getAllSubtasks(), "Удаление задачи по id в менеджере задач некорректно");
    }

    @Test
    void getTaskByIdTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        manager.addTask(task1);
        Task task2 = new Task(task1.getName(), task1.getDescription(), task1.getTaskStatus(), task1.getTaskId());
        assertEquals(task2, manager.getTaskById(task1.getTaskId()), "Задача не возвращается по id");
    }

    @Test
    void getEpicTaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        EpicTask epicTask2 = new EpicTask(epicTask1.getName(), epicTask1.getDescription());
        epicTask2.setTaskStatus(epicTask1.getTaskStatus());
        epicTask2.setTaskId(epicTask1.getTaskId());
        assertEquals(epicTask2, manager.getEpicTaskById(epicTask1.getTaskId()), "Эпик не возвращается по id");
    }

    @Test
    void getSubtaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask(subtask1.getName(), subtask1.getDescription(), epicTask1.getTaskId());
        subtask2.setTaskId(subtask1.getTaskId());
        subtask2.setTaskStatus(subtask1.getTaskStatus());
        assertEquals(subtask2, manager.getSubtaskById(subtask1.getTaskId()), "Подзадача не возвращается по id");
    }

    @Test
    void getAllTasksTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = new Task("Задача 2","Описание задачи 2");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        manager.addTask(task1);
        manager.addTask(task2);
        assertEquals(tasks, manager.getAllTasks(), "Задачи не совпадают");
    }

    @Test
    void getAllEpicTasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2","Описание эпика 2");
        ArrayList<EpicTask> epicTasks = new ArrayList<>();
        epicTasks.add(epicTask1);
        epicTasks.add(epicTask2);
        manager.addEpicTask(epicTask1);
        manager.addEpicTask(epicTask2);
        assertEquals(epicTasks, manager.getAllEpicTasks(), "Эпики не совпадают");
    }

    @Test
    void getAllSubtasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(subtasks, manager.getAllSubtasks(), "Подзадачи не совпадают");
    }

    @Test
    void deleteAllTasksTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = new Task("Задача 2","Описание задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);
        assertNotNull(manager.getAllTasks(), "Задачи не найдены");
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Удаление всех задач работает некорректно");
    }

    @Test
    void deleteAllEpicTasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2","Описание эпика 2");
        manager.addEpicTask(epicTask1);
        manager.addEpicTask(epicTask2);
        assertNotNull(manager.getAllEpicTasks(), "Эпики не найдены");
        manager.deleteAllEpicTasks();
        assertTrue(manager.getAllEpicTasks().isEmpty(), "Удаление всех эпиков работает некорректно");
    }

    @Test
    void deleteAllSubtasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertNotNull(epicTask1.getSubtasks(), "Подзадачи не найдены в эпике");
        assertNotNull(manager.getAllSubtasks(), "Подзадачи не найдены в менеджере");
        manager.deleteAllSubtasks();
        assertTrue(epicTask1.getSubtasks().isEmpty(), "Подзадачи не удалены в эпике");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Подзадачи не удалены в менеджере");
    }

    @Test
    void getEpicSubtasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        assertEquals(subtasks, manager.getEpicSubtasks(epicTask1.getTaskId()), "Подзадачи эпика не найдены");
    }

    @Test
    void getHistoryTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        manager.addTask(task1);
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        manager.addSubtask(subtask1);
        ArrayList<Task> allTasks = new ArrayList<>(manager.getAllSubtasks());
        assertEquals(allTasks, manager.getHistory(), "История просмотров работает некорректно");
    }

    @Test
    void twoTasksWithSameIdAreEquals() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        manager.addTask(task1);
        int taskId1 = task1.getTaskId();
        Task task2 = manager.getTaskById(taskId1);
        task2.setName("Обновлённая задача 1");
        assertEquals(taskId1,task2.getTaskId(), "Id задач не совпадают");
        assertEquals(task2, manager.getTaskById(taskId1), "Задачи не совпадают");
    }

    @Test
    void twoEpicTasksWithSameIdAreEquals() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        int taskId1 = epicTask1.getTaskId();
        EpicTask epicTask2 = manager.getEpicTaskById(taskId1);
        epicTask2.setName("Обновлённый эпик 1");
        assertEquals(taskId1,epicTask2.getTaskId(), "Id эпиков не совпадают");
        assertEquals(epicTask2, manager.getEpicTaskById(taskId1), "Эпики не совпадают");
    }

    @Test
    void twoSubtasksWithSameIdAreEquals() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Задача 1","Описание задачи 1", epicTask1.getTaskId());
        manager.addSubtask(subtask1);
        int taskId1 = subtask1.getTaskId();
        Subtask subtask2 = manager.getSubtaskById(taskId1);
        subtask2.setName("Обновлённая задача 1");
        assertEquals(taskId1,subtask2.getTaskId(), "Id задач не совпадают");
        assertEquals(subtask2, manager.getSubtaskById(taskId1), "Подзадачи не совпадают");
    }

    @Test
    void creationTwoTasksWithSameIdIsImpossible() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        manager.addTask(task1);
        int taskId1 = task1.getTaskId();
        Task task2 = new Task("Задача 2","Описание задачи 2", TaskStatus.NEW);
        task2.setTaskId(taskId1);
        manager.addTask(task2);
        int taskId2 = task2.getTaskId();
        assertNotEquals(taskId1, taskId2, "Id задач должны различаться");
        assertNotEquals(manager.getTaskById(taskId1), manager.getTaskById(taskId2), "Задачи должны различаться");
    }

    @Test
    void taskDoesNotChangeAfterBeingAddedToTheManager() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = task1;
        manager.addTask(task1);
        Task task3 = manager.getTaskById(task1.getTaskId());
        assertEquals(task1.getTaskId(), task3.getTaskId(), "Id задач различаются");
        assertEquals(task2.getName(), task3.getName(), "Названия задач различаются");
        assertEquals(task2.getDescription(), task3.getDescription(), "Описания задач различаются");
    }

    @Test
    void tasksWithCreatedIdDontConflictWithGeneratedId() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = new Task("Задача 2","Описание задачи 2", TaskStatus.NEW, 1);
        manager.addTask(task1);
        assertEquals(task2.getTaskId(), task1.getTaskId(), "Id задач различаются");
        manager.addTask(task2);
        assertNotEquals(task1.getTaskId(), task2.getTaskId(), "Id задач одинаковые");
    }

    @Test
    void epicTaskStatusMustChangeDependingOnStatusSubtasks() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        manager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epicTask1.getTaskStatus(), "Статус эпика не совпадает");
        Subtask subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.IN_PROGRESS, epicTask1.getTaskId());
        manager.updateSubtask(subtask1, subtask3);
        assertEquals(TaskStatus.IN_PROGRESS, epicTask1.getTaskStatus(), "Статус эпика не совпадает");
        subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.DONE, epicTask1.getTaskId());
        manager.updateSubtask(subtask1, subtask3);
        manager.deleteSubtaskById(subtask2);
        assertEquals(TaskStatus.DONE, epicTask1.getTaskStatus(), "Статус эпика не совпадает");
        subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.NEW, epicTask1.getTaskId());
        manager.updateSubtask(subtask1, subtask3);
        assertEquals(TaskStatus.NEW, epicTask1.getTaskStatus(), "Статус эпика не совпадает");
    }
}