package manager;

import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager inMemoryHistoryManager;
    Task task1;
    EpicTask task2;
    Subtask task3;

    @BeforeEach
    void createNewInMemoryHistoryManager() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, 1);
        task2 = new EpicTask("Эпик 2", "Описание эпика 2");
        task3 = new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, 3);
    }

    @Test
    void addTaskTest() {
        inMemoryHistoryManager.add(task1);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        assertEquals(inMemoryHistoryManager.getHead().getElement(), task1, "Первая просмотренная задача некорректна");
        assertNull(inMemoryHistoryManager.getHead().getPrev(), "У первой задачи не может быть связи с предыдущей");
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "Добавление задачи в историю просмотров некорректно");
    }

    @Test
    void addEpicTaskTest() {
        inMemoryHistoryManager.add(task2);
        ArrayList<EpicTask> epicTasks = new ArrayList<>();
        epicTasks.add(task2);
        assertEquals(inMemoryHistoryManager.getHead().getElement(), task2, "Первая просмотренная задача некорректна");
        assertNull(inMemoryHistoryManager.getHead().getPrev(), "У первой задачи не может быть связи с предыдущей");
        assertEquals(epicTasks, inMemoryHistoryManager.getHistory(), "Добавление эпика в историю просмотров некорректно");
    }

    @Test
    void addSubtaskTest() {
        inMemoryHistoryManager.add(task3);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(task3);
        assertEquals(inMemoryHistoryManager.getHead().getElement(), task3, "Первая просмотренная задача некорректна");
        assertNull(inMemoryHistoryManager.getHead().getPrev(), "У первой задачи не может быть связи с предыдущей");
        assertEquals(subtasks, inMemoryHistoryManager.getHistory(), "Добавление подзадачи в историю просмотров некорректно");
    }

    @Test
    void cannotAddVoidTask() {
        Task task = null;
        inMemoryHistoryManager.add(task);
        ArrayList<Task> tasks = new ArrayList<>();
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "Списки истории не совпадают");
    }

    @Test
    void cannotAddDublicatedTasks() {
        inMemoryHistoryManager.add(task1);
        assertEquals(inMemoryHistoryManager.getHead().getElement(), task1, "Первая просмотренная задача некорректна");
        inMemoryHistoryManager.add(task1);
        assertEquals(1, inMemoryHistoryManager.getHistory().size(), "Количество задач в истории просмотров некорректно");
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
    void linkLastWhenInMemoryHistoryManagerAlreadyExistSomeTasks() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "История просмотров не соответствует ожидаемой");
        assertEquals(inMemoryHistoryManager.getHead().getNext().getElement(), task2, "Связь первой и второй задач некорректна");
        assertEquals(inMemoryHistoryManager.getNodesOfTasks().get(task2.getTaskId()).getPrev().getElement(), task1, "Связь первой и второй задач некорректна");
        inMemoryHistoryManager.add(task3);
        tasks.add(task3);
        assertEquals(inMemoryHistoryManager.getNodesOfTasks().get(task2.getTaskId()).getNext().getElement(), task3, "Связь второй и третьей задач некорректна");
        assertEquals(inMemoryHistoryManager.getNodesOfTasks().get(task3.getTaskId()).getPrev().getElement(), task2, "Связь второй и третьей задач некорректна");
        assertNull(inMemoryHistoryManager.getTail().getNext(), "У последней задачи не может быть связи со следующей");
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "История просмотров не соответствует ожидаемой");
    }

    @Test
    void linkLastExistingTaskInMemoryHistoryManager() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        task1.setName("Обновлённое имя задачи");
        inMemoryHistoryManager.add(task1);
        assertEquals(inMemoryHistoryManager.getHead().getNext().getElement(), task1, "Связь первой и второй задач некорректна");
        assertEquals(inMemoryHistoryManager.getNodesOfTasks().get(task1.getTaskId()).getPrev().getElement(), task2, "Связь первой и второй задач некорректна");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task2);
        tasks.add(task1);
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "История просмотров не соответствует ожидаемой");
        assertNull(inMemoryHistoryManager.getTail().getNext(), "У последней задачи не может быть связи со следующей");
    }

    @Test
    void removeTest() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        assertEquals(inMemoryHistoryManager.getHead().getNext().getElement(), task2, "Связь первой и второй задач некорректна");
        assertEquals(inMemoryHistoryManager.getNodesOfTasks().get(task2.getTaskId()).getPrev().getElement(), task1, "Связь первой и второй задач некорректна");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        inMemoryHistoryManager.remove(task2.getTaskId());
        assertNull(inMemoryHistoryManager.getHead().getNext(), "Осталась связь с удалённой задачей");
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "История просмотров не соответствует ожидаемой");
    }

    @Test
    void removeNodeFromBeginning() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task2);
        inMemoryHistoryManager.remove(task1.getTaskId());
        assertNull(inMemoryHistoryManager.getHead().getPrev(), "Осталась связь с удалённой задачей");
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "История просмотров не соответствует ожидаемой");
    }

    @Test
    void removeNodeFromMiddle() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task3);
        inMemoryHistoryManager.remove(task2.getTaskId());
        assertEquals(inMemoryHistoryManager.getHead().getNext().getElement(), task3, "Связь первой и второй задач некорректна");
        assertEquals(inMemoryHistoryManager.getNodesOfTasks().get(task3.getTaskId()).getPrev().getElement(), task1, "Связь первой и второй задач некорректна");
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "История просмотров не соответствует ожидаемой");
    }

    @Test
    void removeNodeFromEnding() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        inMemoryHistoryManager.remove(task2.getTaskId());
        assertNull(inMemoryHistoryManager.getHead().getNext(), "Осталась связь с удалённой задачей");
        assertEquals(tasks, inMemoryHistoryManager.getHistory(), "История просмотров не соответствует ожидаемой");
    }
}