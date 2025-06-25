package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    Task task;

    @BeforeEach
    void createNewTask() {
        task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, 1);
    }

    @Test
    void getTaskStatusTest() {
        assertEquals(TaskStatus.NEW, task.getTaskStatus(), "Статус задачи не соотвестствует ожидаемому");
    }

    @Test
    void setTaskStatusTest() {
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getTaskStatus(), "Статус задачи после его обновления не соотвестствует ожидаемому");
    }

    @Test
    void getTaskNameTest() {
        assertEquals("Задача 1", task.getName(), "Название задачи не соотвестствует ожидаемому");
    }

    @Test
    void setTaskNameTest() {
        task.setName("Изменённое имя задачи 1");
        assertEquals("Изменённое имя задачи 1", task.getName(), "Название задачи после его обновления не соотвестствует ожидаемому");
    }

    @Test
    void getTaskDescriptionTest() {
        assertEquals("Описание задачи 1", task.getDescription(), "Описание задачи не соотвестствует ожидаемому");
    }

    @Test
    void setTaskDescriptionTest() {
        task.setDescription("Изменённое описание задачи 1");
        assertEquals("Изменённое описание задачи 1", task.getDescription(), "Описание задачи после его обновления не соотвестствует ожидаемому");
    }

    @Test
    void getTaskTaskIdTest() {
        assertEquals(1, task.getTaskId(), "Id задачи не соотвестствует ожидаемому");
    }

    @Test
    void setTaskTaskIdTest() {
        task.setTaskId(2);
        assertEquals(2, task.getTaskId(), "Id задачи после его обновления не соотвестствует ожидаемому");
    }

    @Test
    void getEndTime() {

    }

    @Test
    void setDuration() {
        Duration duration = Duration.ofMinutes(15L);
        task.setDuration(duration);
        assertEquals(duration.toString(), task.getDuration().toString(), "Продолжительность выполнения задачи добавляется некорректно");
    }

    @Test
    void setStartTime() {
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 6, 14, 0);
        task.setStartTime(startTime);
        assertEquals(startTime.toString(), task.getStartTime().toString(), "Время начала выполнения задачи добавляется некорректно");
    }
}