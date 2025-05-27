package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
class EpicTaskTest {

    EpicTask epicTask;

    @BeforeEach
    void createNewEpicTask() {
        epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        Subtask subtask = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1",TaskStatus.NEW, epicTask.getTaskId());
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
}