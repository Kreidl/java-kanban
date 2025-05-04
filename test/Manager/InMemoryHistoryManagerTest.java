package Manager;

import Tasks.EpicTask;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager inMemoryHistoryManager;
    Task task1;
    EpicTask task2;
    Subtask task3;

    @BeforeEach
    void createNewInMemoryHistoryManager() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, 1);
        task2 = new EpicTask("Задача 2", "Описание задачи 2");
        task3 = new Subtask("Задача 3", "Описание задачи 3", TaskStatus.NEW, 3);
    }

    @Test
    void addTaskTest() {
        inMemoryHistoryManager.add(task1);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "Добавление задачи в историю просмотров некорректно");
    }

    @Test
    void addEpicTaskTest() {
        inMemoryHistoryManager.add(task2);
        ArrayList<EpicTask> epicTasks = new ArrayList<>();
        epicTasks.add(task2);
        assertEquals(epicTasks, inMemoryHistoryManager.getHistory(), "Добавление эпика в историю просмотров некорректно");
    }

    @Test
    void addSubtaskTest() {
        inMemoryHistoryManager.add(task3);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(task3);
        assertEquals(subtasks, inMemoryHistoryManager.getHistory(), "Добавление подзадачи в историю просмотров некорректно");
    }

    @Test
    void getHistoryTest() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "История просмотров не соответствует ожидаемой");
    }

    @Test
    void cannotAddVoidTask() {
        Task task = null;
        inMemoryHistoryManager.add(task);
        ArrayList<Task> tasks = new ArrayList<>();
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "Списки истории не совпадают");
    }

    @Test
    void cannotAddMoreThen10Tasks() {
        Task task4 = new Task("Задача 4", "Описание задачи 4", TaskStatus.NEW, 4);
        Task task5 = new Task("Задача 5", "Описание задачи 5", TaskStatus.NEW, 5);
        Task task6 = new Task("Задача 6", "Описание задачи 6", TaskStatus.NEW, 6);
        Task task7 = new Task("Задача 7", "Описание задачи 7", TaskStatus.NEW, 7);
        Task task8 = new Task("Задача 8", "Описание задачи 8", TaskStatus.NEW, 8);
        Task task9 = new Task("Задача 9", "Описание задачи 9", TaskStatus.NEW, 9);
        Task task10 = new Task("Задача 10", "Описание задачи 10", TaskStatus.NEW, 10);
        Task task11 = new Task("Задача 11", "Описание задачи 11", TaskStatus.NEW, 11);
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.add(task2);
        allTasks.add(task3);
        allTasks.add(task4);
        allTasks.add(task5);
        allTasks.add(task6);
        allTasks.add(task7);
        allTasks.add(task8);
        allTasks.add(task9);
        allTasks.add(task10);
        allTasks.add(task11);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.add(task4);
        inMemoryHistoryManager.add(task5);
        inMemoryHistoryManager.add(task6);
        inMemoryHistoryManager.add(task7);
        inMemoryHistoryManager.add(task8);
        inMemoryHistoryManager.add(task9);
        inMemoryHistoryManager.add(task10);
        inMemoryHistoryManager.add(task11);
        assertEquals(allTasks, inMemoryHistoryManager.getHistory(), "Добавление задачи в заполненную историю просмотров некорректно");
    }

    @Test
    void tasksInHistoryManagerSavedPreviousVersionTask() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        task1.setName("Обновлённое имя задачи");
        inMemoryHistoryManager.add(task1);
        tasks.add(task1);
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "Задачи в истории просмотров не сохраняют предыдущую версию задачи");
    }
}