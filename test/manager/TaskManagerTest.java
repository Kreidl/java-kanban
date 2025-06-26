package manager;

import exceptions.TaskIntersectWithOther;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    static Task exampleTask;
    static EpicTask exampleEpicTask;
    static Subtask exampleSubtask;

    public abstract void createNewTaskManager();

    @BeforeEach
    void createNewTaskManagerAndExamplesOfTasks() {
        createNewTaskManager();
        exampleTask = new Task("Задача 100","Описание задачи 100");
        exampleEpicTask = new EpicTask("Эпик 100", "Описание эпика 100");
        exampleSubtask = new Subtask("Подзадача 100.1","Описание подзадачи 100.1", 2);
    }

    @Test
    void addTaskTest() {
        Task task1 = new Task(exampleTask.getName(),exampleTask.getDescription());
        exampleTask.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        exampleTask.setDuration(Duration.ofMinutes(15));
        taskManager.addTask(exampleTask);
        assertEquals(task1.getClass(), taskManager.getTaskById(exampleTask.getTaskId()).getClass(), "Задача нужного класса не найдена");
        assertEquals(task1.getName(), taskManager.getTaskById(exampleTask.getTaskId()).getName(), "Задача с заданным именем не добавлена");
        assertEquals(task1.getDescription(), taskManager.getTaskById(exampleTask.getTaskId()).getDescription(), "Задача с заданным описанием не добавлена");
        TreeSet<Task> testTasks = new TreeSet<>((t1, t2) -> {
            if (t1.getStartTime().isBefore(t2.getStartTime())) {
                return 1;
            } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return -1;
            }
            return 0;
        });;
        testTasks.add(exampleTask);
        assertEquals(testTasks ,taskManager.getPrioritizedTasks(), "Задача добавлена в список приоритета некорректно");
    }

    @Test
    void addEpicTaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        EpicTask epicTask2 = epicTask1;
        taskManager.addEpicTask(epicTask1);
        assertEquals(exampleEpicTask.getClass(), taskManager.getEpicTaskById(epicTask1.getTaskId()).getClass());
        assertEquals(epicTask2.getName(), epicTask1.getName(), "Эпик с заданным именем не добавлен");
        assertEquals(epicTask2.getDescription(), epicTask1.getDescription(), "Эпик с заданным описанием не добавлен");
    }

    @Test
    void addSubtaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        subtask1.setDuration(Duration.ofMinutes(15));
        Subtask subtask2 = subtask1;
        taskManager.addSubtask(subtask1);
        assertEquals(exampleSubtask.getClass(), taskManager.getSubtaskById(subtask1.getTaskId()).getClass());
        assertEquals(subtask2.getName(), taskManager.getSubtaskById(subtask1.getTaskId()).getName(), "Подзадача с заданным именем не добавлена в менеджер задач");
        assertEquals(subtask2.getDescription(), taskManager.getSubtaskById(subtask1.getTaskId()).getDescription(), "Подзадача с заданным описанием не добавлена в менеджер задач");
        int indexSubtask = epicTask1.getSubtasks().indexOf(subtask1);
        assertEquals(exampleSubtask.getClass(), epicTask1.getSubtasks().get(indexSubtask).getClass());
        assertEquals(subtask2.getName(), epicTask1.getSubtasks().get(indexSubtask).getName(), "Подзадача с заданным именем не добавлена в эпик");
        assertEquals(subtask2.getDescription(), epicTask1.getSubtasks().get(indexSubtask).getDescription(),"Подзадача с заданным описанием не добавлена в эпик");
        TreeSet<Task> testTasks = new TreeSet<>((t1, t2) -> {
            if (t1.getStartTime().isBefore(t2.getStartTime())) {
                return 1;
            } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return -1;
            }
            return 0;
        });
        testTasks.add(subtask1);
        assertEquals(testTasks ,taskManager.getPrioritizedTasks(), "Подзадача добавлена в список приоритета некорректно");
    }

    @Test
    void cannotAddVoidTask() {
        Task task = null;
        taskManager.addTask(task);
        ArrayList<Task> tasks= new ArrayList<>();
        assertEquals(tasks, taskManager.getAllTasks(), "Списки задач не совпадают");
    }

    @Test
    void cannotAddVoidEpicTask() {
        EpicTask epicTask = null;
        taskManager.addEpicTask(epicTask);
        ArrayList<EpicTask> epicTasks= new ArrayList<>();
        assertEquals(epicTasks, taskManager.getAllEpicTasks(), "Списки эпиков не совпадают");
    }

    @Test
    void cannotAddVoidSubtask() {
        Subtask subtask = null;
        taskManager.addSubtask(subtask);
        ArrayList<Subtask> subtasks= new ArrayList<>();
        assertEquals(subtasks, taskManager.getAllSubtasks(), "Списки подзадач не совпадают");
    }

    @Test
    void updateTaskTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        task1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        task1.setDuration(Duration.ofMinutes(15));
        taskManager.addTask(task1);
        Task task2 = new Task("Обновлённое название задачи 1", "Обновлённое описание задачи 1", TaskStatus.IN_PROGRESS);
        task2.setStartTime(LocalDateTime.of(2025, 5, 7, 14, 0));
        task2.setDuration(Duration.ofMinutes(30));
        taskManager.updateTask(task1, task2);
        assertEquals(task1, task2, "Задача не обновлена");
        assertEquals(task2.getStartTime(), taskManager.getPrioritizedTasks().getFirst().getStartTime(), "Задача в списке приоритета не обновлена");
        assertEquals(task2.getDuration(), taskManager.getPrioritizedTasks().getFirst().getDuration(), "Задача в списке приоритета не обновлена");
    }

    @Test
    void updateEpicTaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        EpicTask epicTask2 = new EpicTask("Название обновлённого эпика 1","Описание обновлённого эпика 1");
        taskManager.updateEpicTask(epicTask1, epicTask2);
        assertEquals(epicTask1, epicTask2, "Эпик не обновлен");
    }

    @Test
    void updateSubtaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        subtask1.setDuration(Duration.ofMinutes(15));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Обновлённое название подзадачи 1.1","Обновлённое описание подзадачи 1.1", epicTask1.getTaskId());
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 7, 14, 0));
        subtask2.setDuration(Duration.ofMinutes(30));
        taskManager.updateSubtask(subtask1, subtask2);
        int indexSubtask = epicTask1.getSubtasks().indexOf(subtask1);
        assertEquals(subtask2, epicTask1.getSubtasks().get(indexSubtask), "Подзадача в эпике не обновлена");
        assertEquals(subtask2, subtask1, "Подзадача в менеджере задач не обновлена");
        assertEquals(subtask2.getStartTime(), taskManager.getPrioritizedTasks().getFirst().getStartTime(), "Подзадача в списке приоритета не обновлена");
        assertEquals(subtask2.getDuration(), taskManager.getPrioritizedTasks().getFirst().getDuration(), "Подзадача в списке приоритета не обновлена");
    }

    @Test
    void deleteTaskByIdTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        task1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        task1.setDuration(Duration.ofMinutes(15));
        taskManager.addTask(task1);
        assertNotNull(taskManager.getTaskById(task1.getTaskId()));
        assertNotNull(taskManager.getPrioritizedTasks());
        taskManager.deleteTaskById(taskManager.getTaskById(task1.getTaskId()));
        assertNull(taskManager.getTaskById(task1.getTaskId()), "Удаление задачи по id некорректно");
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Задача из списка приоритета не удалена");
    }

    @Test
    void deleteEpicTaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        assertNotNull(taskManager.getEpicTaskById(epicTask1.getTaskId()));
        taskManager.deleteEpicTaskById(taskManager.getEpicTaskById(epicTask1.getTaskId()));
        assertNull(taskManager.getEpicTaskById(epicTask1.getTaskId()), "Удаление эпика по id некорректно");
    }

    @Test
    void deletePrioritizedSubtaskWithTimeAfterDeletingItsEpicTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        subtask1.setDuration(Duration.ofMinutes(15));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 7, 14, 0));
        subtask2.setDuration(Duration.ofMinutes(30));
        taskManager.addSubtask(subtask2);
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Количество задач в списке приоритета некорректно");
        taskManager.deleteEpicTaskById(taskManager.getEpicTaskById(epicTask1.getTaskId()));
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Подзадачи после удаления эпика не удалены из списка приоритета");
    }

    @Test
    void deleteSubtaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        subtask1.setDuration(Duration.ofMinutes(15));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 7, 14, 0));
        subtask2.setDuration(Duration.ofMinutes(30));
        taskManager.addSubtask(subtask2);
        assertNotNull(taskManager.getSubtaskById(subtask1.getTaskId()), "Подзадача, добавленная в менеджер задач, не может быть пустой");
        assertNotNull(taskManager.getSubtaskById(subtask2.getTaskId()), "Подзадача, добавленная в менеджер задач, не может быть пустой");
        ArrayList<Subtask> subtasksTest = new ArrayList<>();
        subtasksTest.add(subtask1);
        taskManager.deleteSubtaskById(taskManager.getSubtaskById(subtask2.getTaskId()));
        assertEquals(subtasksTest, epicTask1.getSubtasks(), "Удаление подзадачи по id в эпике некорректно");
        assertEquals(subtasksTest, taskManager.getAllSubtasks(), "Удаление задачи по id в менеджере задач некорректно");
        assertEquals(taskManager.getSubtaskById(subtask1.getTaskId()), taskManager.getPrioritizedTasks().getFirst(), "Удалена не та подзадача в списке приоритета");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Подзадача в списке приоритета не удалена");
    }

    @Test
    void getTaskByIdTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        taskManager.addTask(task1);
        Task task2 = new Task(task1.getName(), task1.getDescription(), task1.getTaskStatus(), task1.getTaskId());
        assertEquals(task2, taskManager.getTaskById(task1.getTaskId()), "Задача не возвращается по id");
    }

    @Test
    void getEpicTaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        EpicTask epicTask2 = new EpicTask(epicTask1.getName(), epicTask1.getDescription());
        epicTask2.setTaskStatus(epicTask1.getTaskStatus());
        epicTask2.setTaskId(epicTask1.getTaskId());
        assertEquals(epicTask2, taskManager.getEpicTaskById(epicTask1.getTaskId()), "Эпик не возвращается по id");
    }

    @Test
    void getSubtaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask(subtask1.getName(), subtask1.getDescription(), epicTask1.getTaskId());
        subtask2.setTaskId(subtask1.getTaskId());
        subtask2.setTaskStatus(subtask1.getTaskStatus());
        assertEquals(subtask2, taskManager.getSubtaskById(subtask1.getTaskId()), "Подзадача не возвращается по id");
    }

    @Test
    void getAllTasksTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = new Task("Задача 2","Описание задачи 2");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertEquals(tasks, taskManager.getAllTasks(), "Задачи не совпадают");
    }

    @Test
    void getAllEpicTasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2","Описание эпика 2");
        ArrayList<EpicTask> epicTasks = new ArrayList<>();
        epicTasks.add(epicTask1);
        epicTasks.add(epicTask2);
        taskManager.addEpicTask(epicTask1);
        taskManager.addEpicTask(epicTask2);
        assertEquals(epicTasks, taskManager.getAllEpicTasks(), "Эпики не совпадают");
    }

    @Test
    void getAllSubtasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(subtasks, taskManager.getAllSubtasks(), "Подзадачи не совпадают");
    }

    @Test
    void deleteAllTasksTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        task1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        task1.setDuration(Duration.ofMinutes(15));
        Task task2 = new Task("Задача 2","Описание задачи 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Количество задач в списке приоритета некорректно");
        assertNotNull(taskManager.getAllTasks(), "Задачи не найдены");
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Удаление всех задач работает некорректно");
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Задачи не удалены из списка приоритета");
    }

    @Test
    void deleteAllEpicTasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2","Описание эпика 2");
        taskManager.addEpicTask(epicTask1);
        taskManager.addEpicTask(epicTask2);
        assertNotNull(taskManager.getAllEpicTasks(), "Эпики не найдены");
        taskManager.deleteAllEpicTasks();
        assertTrue(taskManager.getAllEpicTasks().isEmpty(), "Удаление всех эпиков работает некорректно");
    }

    @Test
    void deleteAllSubtasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        subtask1.setDuration(Duration.ofMinutes(15));
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Количество задач в списке приоритета некорректно");
        assertNotNull(epicTask1.getSubtasks(), "Подзадачи не найдены в эпике");
        assertNotNull(taskManager.getAllSubtasks(), "Подзадачи не найдены в менеджере");
        taskManager.deleteAllSubtasks();
        assertTrue(epicTask1.getSubtasks().isEmpty(), "Подзадачи не удалены в эпике");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалены в менеджере");
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Задачи не удалены из списка приоритета");
    }

    @Test
    void getEpicSubtasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        assertEquals(subtasks, taskManager.getEpicSubtasks(epicTask1.getTaskId()), "Подзадачи эпика не найдены");
    }

    @Test
    void getHistoryTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        taskManager.addTask(task1);
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        taskManager.addSubtask(subtask1);
        ArrayList<Task> allTasks = new ArrayList<>(taskManager.getAllSubtasks());
        assertEquals(allTasks, taskManager.getHistory(), "История просмотров работает некорректно");
    }

    @Test
    void getHistoryAfterRemovingEpicTaskWithSubtasks() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addTask(task1);
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.NEW, epicTask1.getTaskId());
        taskManager.addSubtask(subtask1);
        taskManager.getAllTasks();
        taskManager.getAllEpicTasks();
        taskManager.getAllSubtasks();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        taskManager.deleteEpicTaskById(epicTask1);
        assertEquals(tasks, taskManager.getHistory(), "История просмотров не соответствует ожидаемой");
    }

    @Test
    void twoTasksWithSameIdAreEquals() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        taskManager.addTask(task1);
        int taskId1 = task1.getTaskId();
        Task task2 = taskManager.getTaskById(taskId1);
        task2.setName("Обновлённая задача 1");
        assertEquals(taskId1,task2.getTaskId(), "Id задач не совпадают");
        assertEquals(task2, taskManager.getTaskById(taskId1), "Задачи не совпадают");
    }

    @Test
    void twoEpicTasksWithSameIdAreEquals() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        int taskId1 = epicTask1.getTaskId();
        EpicTask epicTask2 = taskManager.getEpicTaskById(taskId1);
        epicTask2.setName("Обновлённый эпик 1");
        assertEquals(taskId1,epicTask2.getTaskId(), "Id эпиков не совпадают");
        assertEquals(epicTask2, taskManager.getEpicTaskById(taskId1), "Эпики не совпадают");
    }

    @Test
    void twoSubtasksWithSameIdAreEquals() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Задача 1","Описание задачи 1", epicTask1.getTaskId());
        taskManager.addSubtask(subtask1);
        int taskId1 = subtask1.getTaskId();
        Subtask subtask2 = taskManager.getSubtaskById(taskId1);
        subtask2.setName("Обновлённая задача 1");
        assertEquals(taskId1,subtask2.getTaskId(), "Id задач не совпадают");
        assertEquals(subtask2, taskManager.getSubtaskById(taskId1), "Подзадачи не совпадают");
    }

    @Test
    void creationTwoTasksWithSameIdIsImpossible() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        taskManager.addTask(task1);
        int taskId1 = task1.getTaskId();
        Task task2 = new Task("Задача 2","Описание задачи 2", TaskStatus.NEW);
        task2.setTaskId(taskId1);
        taskManager.addTask(task2);
        int taskId2 = task2.getTaskId();
        assertNotEquals(taskId1, taskId2, "Id задач должны различаться");
        assertNotEquals(taskManager.getTaskById(taskId1), taskManager.getTaskById(taskId2), "Задачи должны различаться");
    }

    @Test
    void taskDoesNotChangeAfterBeingAddedToTheManager() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = task1;
        taskManager.addTask(task1);
        Task task3 = taskManager.getTaskById(task1.getTaskId());
        assertEquals(task1.getTaskId(), task3.getTaskId(), "Id задач различаются");
        assertEquals(task2.getName(), task3.getName(), "Названия задач различаются");
        assertEquals(task2.getDescription(), task3.getDescription(), "Описания задач различаются");
    }

    @Test
    void tasksWithCreatedIdDontConflictWithGeneratedId() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = new Task("Задача 2","Описание задачи 2", TaskStatus.NEW, 1);
        taskManager.addTask(task1);
        assertEquals(task2.getTaskId(), task1.getTaskId(), "Id задач различаются");
        taskManager.addTask(task2);
        assertNotEquals(task1.getTaskId(), task2.getTaskId(), "Id задач одинаковые");
    }

    @Test
    void epicTaskStatusMustChangeDependingOnStatusSubtasks() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epicTask1.getTaskStatus(), "Статус эпика не совпадает");
        Subtask subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.IN_PROGRESS, epicTask1.getTaskId());
        taskManager.updateSubtask(subtask1, subtask3);
        assertEquals(TaskStatus.IN_PROGRESS, epicTask1.getTaskStatus(), "Статус эпика не совпадает");
        subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.DONE, epicTask1.getTaskId());
        taskManager.updateSubtask(subtask1, subtask3);
        taskManager.deleteSubtaskById(subtask2);
        assertEquals(TaskStatus.DONE, epicTask1.getTaskStatus(), "Статус эпика не совпадает");
        subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.NEW, epicTask1.getTaskId());
        taskManager.updateSubtask(subtask1, subtask3);
        assertEquals(TaskStatus.NEW, epicTask1.getTaskStatus(), "Статус эпика не совпадает");
    }

    @Test
    void exceptionAddPrioritizedTasksWithIntersectTimeTest() {
        //Когда окончание первой задачи пересекается с началом второй задачи
        TaskIntersectWithOther exception = Assertions.assertThrows(TaskIntersectWithOther.class, () -> {
            Task task1 = new Task("Задача 1", "Описание задачи 1");
            task1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
            task1.setDuration(Duration.ofMinutes(15));
            taskManager.addTask(task1);
            EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
            taskManager.addEpicTask(epicTask1);
            Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask1.getTaskId());
            subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 10));
            subtask1.setDuration(Duration.ofMinutes(15));
            taskManager.addSubtask(subtask1);
        });
        assertEquals("Задача пересекается с другой" ,exception.getMessage(), "Пересекающиеся задачи работают некорректно");
        //Когда начало первой задачи пересекается с окончанием второй задачи
        exception = Assertions.assertThrows(TaskIntersectWithOther.class, () -> {
            Task task1 = new Task("Задача 1", "Описание задачи 1");
            task1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
            task1.setDuration(Duration.ofMinutes(15));
            taskManager.addTask(task1);
            EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
            taskManager.addEpicTask(epicTask1);
            Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask1.getTaskId());
            subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 13, 50));
            subtask1.setDuration(Duration.ofMinutes(15));
            taskManager.addSubtask(subtask1);
        });
        assertEquals("Задача пересекается с другой" ,exception.getMessage(), "Пересекающиеся задачи работают некорректно");
        //Когда начало и конец первой задачи совпадают с началом и концом второй задачи
        exception = Assertions.assertThrows(TaskIntersectWithOther.class, () -> {
            Task task1 = new Task("Задача 1", "Описание задачи 1");
            task1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
            task1.setDuration(Duration.ofMinutes(15));
            taskManager.addTask(task1);
            EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
            taskManager.addEpicTask(epicTask1);
            Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask1.getTaskId());
            subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
            subtask1.setDuration(Duration.ofMinutes(15));
            taskManager.addSubtask(subtask1);
        });
        assertEquals("Задача пересекается с другой" ,exception.getMessage(), "Пересекающиеся задачи работают некорректно");
        //Когда начало и конец одной задачи находятся внутри продолжительности другой задачи
        exception = Assertions.assertThrows(TaskIntersectWithOther.class, () -> {
            Task task1 = new Task("Задача 1", "Описание задачи 1");
            task1.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
            task1.setDuration(Duration.ofMinutes(15));
            taskManager.addTask(task1);
            EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
            taskManager.addEpicTask(epicTask1);
            Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask1.getTaskId());
            subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 13, 50));
            subtask1.setDuration(Duration.ofMinutes(30));
            taskManager.addSubtask(subtask1);
        });
        assertEquals("Задача пересекается с другой" ,exception.getMessage(), "Пересекающиеся задачи работают некорректно");
    }
}
