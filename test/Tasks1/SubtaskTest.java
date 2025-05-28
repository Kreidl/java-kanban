package Tasks1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    Subtask subtask;

    @BeforeEach
    void createNewSubtask() {
        subtask = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", TaskStatus.NEW, 1);
    }

    @Test
    void getEpicId() {
        assertEquals(1, subtask.getEpicId(), "Id эпика в подзадаче не соответствует ожидаемому");
    }

    @Test
    void setEpicId() {
        subtask.setEpicId(2);
        assertEquals(2, subtask.getEpicId(), "Id эпика в подзадаче после изменения не соответствует ожидаемому");
    }
}