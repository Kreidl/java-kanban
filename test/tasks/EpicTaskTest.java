package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
class EpicTaskTest {

    EpicTask epicTask;

    @BeforeEach
    void createNewEpicTask() {
        epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", TaskStatus.NEW, epicTask.getTaskId());
        Subtask subtask1 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2",TaskStatus.NEW, epicTask.getTaskId());
        epicTask.getSubtasks().add(subtask);
        epicTask.getSubtasks().add(subtask1);
    }

    @Test
    void getSubtasksTest() {
        ArrayList<Subtask> subtasksTest = new ArrayList<>();
        Subtask subtask = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", TaskStatus.NEW, epicTask.getTaskId());
        Subtask subtask1 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", TaskStatus.NEW, epicTask.getTaskId());
        subtasksTest.add(subtask);
        subtasksTest.add(subtask1);
        assertEquals(subtasksTest, epicTask.getSubtasks(), "Список подзадач эпика не соответствует ожидаемому");
    }

    @Test
    void removeSubtaskTest() {
        ArrayList<Subtask> subtasksTest = new ArrayList<>();
        Subtask subtask = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1",TaskStatus.NEW, epicTask.getTaskId());
        subtasksTest.add(subtask);
        Subtask subtask1 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2",TaskStatus.NEW, epicTask.getTaskId());
        epicTask.removeSubtask(subtask1);
        assertEquals(subtasksTest, epicTask.getSubtasks(), "Список подзадач эпика, после удаления одной подзадачи, не соответствует ожидаемому");
    }

    @Test
    public void setDurationTest() {
        LocalDateTime startTime1 = LocalDateTime.of(2025, 6, 5, 12, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2025, 6, 6, 12, 0);
        Duration duration1 = Duration.ofMinutes(15L);
        Duration duration2 = Duration.ofMinutes(20L);
        Duration duration3 = Duration.between(startTime1, startTime2).plus(duration2);
        epicTask.getSubtasks().get(0).setStartTime(startTime1);
        epicTask.getSubtasks().get(1).setStartTime(startTime2);
        epicTask.getSubtasks().get(0).setDuration(duration1);
        epicTask.getSubtasks().get(1).setDuration(duration2);
        epicTask.getSubtasks().get(0).setEndTime();
        epicTask.getSubtasks().get(1).setEndTime();
        epicTask.setDuration();
        assertEquals(duration3.toString(), epicTask.getDuration().toString(), "Продолжительность выполнения эпика добавляется некорректно");
    }

    @Test
    public void setStartTimeTest() {
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 6, 14, 0);
        epicTask.getSubtasks().get(0).setStartTime(startTime);
        epicTask.setStartTime();
        assertEquals(startTime.toString(), epicTask.getStartTime().toString(), "Дата начала выполнения эпика добавляется некорректно");
    }

    @Test
    public void setEndTimeTest() {
        LocalDateTime startTime1 = LocalDateTime.of(2025, 6, 5, 12, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2025, 6, 6, 12, 0);
        Duration duration1 = Duration.ofMinutes(15L);
        Duration duration2 = Duration.ofMinutes(20L);
        epicTask.getSubtasks().get(0).setStartTime(startTime1);
        epicTask.getSubtasks().get(1).setStartTime(startTime2);
        epicTask.getSubtasks().get(0).setDuration(duration1);
        epicTask.getSubtasks().get(1).setDuration(duration2);
        epicTask.getSubtasks().get(0).setEndTime();
        epicTask.getSubtasks().get(1).setEndTime();
        epicTask.setEndTime();
        LocalDateTime endTime = epicTask.getSubtasks().get(1).getEndTime();
        assertEquals(endTime.toString(), epicTask.getEndTime().toString(), "Дата окончания выполнения эпика добавляется некорректно");
    }
}