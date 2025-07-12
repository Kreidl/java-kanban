package manager;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest {
    static FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    @Override
    public void createNewTaskManager() {
        try {
            File file = File.createTempFile("test", ".csv");
            fileBackedTaskManager = new FileBackedTaskManager(file);
            super.taskManager = fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Не получилось создать файл.");
        }
    }

    @Test
    void addTaskTest() {
        fileBackedTaskManager.addTask(exampleTask);
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" +
                fileBackedTaskManager.toString(exampleTask);
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Задача добавлена некорректно");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void addEpicTaskTest() {
        fileBackedTaskManager.addEpicTask(exampleEpicTask);
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" +
                fileBackedTaskManager.toString(exampleEpicTask);
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Эпик добавлен некорректно");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void addSubtaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        fileBackedTaskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        fileBackedTaskManager.addSubtask(subtask1);
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(epicTask1) +
                fileBackedTaskManager.toString(subtask1);
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Подзадача добавлена некорректно");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void updateTaskTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        fileBackedTaskManager.addTask(task1);
        Task task2 = new Task("Обновлённое название задачи 1", "Обновлённое описание задачи 1", TaskStatus.IN_PROGRESS);
        fileBackedTaskManager.updateTask(task1, task2);
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(task1);
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Задача не обновлена");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void updateEpicTaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        fileBackedTaskManager.addEpicTask(epicTask1);
        EpicTask epicTask2 = new EpicTask("Название обновлённого эпика 1","Описание обновлённого эпика 1");
        fileBackedTaskManager.updateEpicTask(epicTask1, epicTask2);
        assertEquals(epicTask1, epicTask2, "Эпик не обновлен");
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(epicTask1);
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Эпик не обновлён");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void updateSubtaskTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        fileBackedTaskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        fileBackedTaskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Обновлённое название подзадачи 1.1","Обновлённое описание подзадачи 1.1", epicTask1.getTaskId());
        fileBackedTaskManager.updateSubtask(subtask1, subtask2);
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" +
                fileBackedTaskManager.toString(epicTask1) + fileBackedTaskManager.toString(subtask1);
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Подзадача не обновлена");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void deleteTaskByIdTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        fileBackedTaskManager.addTask(task1);
        assertNotNull(fileBackedTaskManager.getTaskById(task1.getTaskId()));
        fileBackedTaskManager.deleteTaskById(fileBackedTaskManager.getTaskById(task1.getTaskId()));
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic";
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Задача удалена некорректно");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void deleteEpicTaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        fileBackedTaskManager.addEpicTask(epicTask1);
        assertNotNull(fileBackedTaskManager.getEpicTaskById(epicTask1.getTaskId()));
        fileBackedTaskManager.deleteEpicTaskById(fileBackedTaskManager.getEpicTaskById(epicTask1.getTaskId()));
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic";
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Эпик удалён некорректно");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void deleteSubtaskByIdTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        fileBackedTaskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        fileBackedTaskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        fileBackedTaskManager.addSubtask(subtask2);
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic"
                + fileBackedTaskManager.toString(epicTask1) + fileBackedTaskManager.toString(subtask1);
        fileBackedTaskManager.deleteSubtaskById(fileBackedTaskManager.getSubtaskById(subtask2.getTaskId()));
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Подзадача удалена некорректно");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void deleteAllTasksTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        Task task2 = new Task("Задача 2","Описание задачи 2");
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        assertNotNull(fileBackedTaskManager.getAllTasks(), "Задачи не найдены");
        fileBackedTaskManager.deleteAllTasks();
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic";
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Задачи не удалены");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void deleteAllEpicTasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2","Описание эпика 2");
        fileBackedTaskManager.addEpicTask(epicTask1);
        fileBackedTaskManager.addEpicTask(epicTask2);
        assertNotNull(fileBackedTaskManager.getAllEpicTasks(), "Эпики не найдены");
        fileBackedTaskManager.deleteAllEpicTasks();
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic";
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Эпики не удалены");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void deleteAllSubtasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        fileBackedTaskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        fileBackedTaskManager.addSubtask(subtask1);
        fileBackedTaskManager.addSubtask(subtask2);
        assertNotNull(epicTask1.getSubtasks(), "Подзадачи не найдены в эпике");
        assertNotNull(fileBackedTaskManager.getAllSubtasks(), "Подзадачи не найдены в менеджере");
        fileBackedTaskManager.deleteAllSubtasks();
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(epicTask1);
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Подзадачи не удалены");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void epicTaskStatusMustChangeDependingOnStatusSubtasksTest() {
        EpicTask epicTask1 = new EpicTask("Эпик 1","Описание эпика 1");
        fileBackedTaskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", epicTask1.getTaskId());
        Subtask subtask2 = new Subtask("Подзадача 1.2","Описание подзадачи 1.2", epicTask1.getTaskId());
        fileBackedTaskManager.addSubtask(subtask1);
        fileBackedTaskManager.addSubtask(subtask2);
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(epicTask1)
                + fileBackedTaskManager.toString(subtask1) + fileBackedTaskManager.toString(subtask2);
        try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Обновление статуса эпика некорректно");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Файл не найден");
            } catch (IOException e) {
                throw new RuntimeException("Не получилось прочитать файл");
            }
            Subtask subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.IN_PROGRESS, epicTask1.getTaskId());
            fileBackedTaskManager.updateSubtask(subtask1, subtask3);
            expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(epicTask1)
                    + fileBackedTaskManager.toString(subtask1) + fileBackedTaskManager.toString(subtask2);
            try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
                String actualTask = "";
                while (br.ready()) {
                    actualTask = actualTask + br.readLine();
                }
                assertEquals(expectedTask, actualTask, "Обновление статуса эпика некорректно");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Файл не найден");
            } catch (IOException e) {
                throw new RuntimeException("Не получилось прочитать файл");
            }
            subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.DONE, epicTask1.getTaskId());
            fileBackedTaskManager.updateSubtask(subtask1, subtask3);
            fileBackedTaskManager.deleteSubtaskById(subtask2);
            expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(epicTask1)
                    + fileBackedTaskManager.toString(subtask1);
            try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
                String actualTask = "";
                while (br.ready()) {
                    actualTask = actualTask + br.readLine();
                }
                assertEquals(expectedTask, actualTask, "Обновление статуса эпика некорректно");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Файл не найден");
            } catch (IOException e) {
                throw new RuntimeException("Не получилось прочитать файл");
            }
            subtask3 = new Subtask("Подзадача 1.1","Описание подзадачи 1.1", TaskStatus.NEW, epicTask1.getTaskId());
            fileBackedTaskManager.updateSubtask(subtask1, subtask3);
                expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(epicTask1)
                    + fileBackedTaskManager.toString(subtask1);
            try (FileReader fr = new FileReader(fileBackedTaskManager.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
                String actualTask = "";
                while (br.ready()) {
                    actualTask = actualTask + br.readLine();
                }
                assertEquals(expectedTask, actualTask, "Обновление статуса эпика некорректно");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Файл не найден");
            } catch (IOException e) {
                throw new RuntimeException("Не получилось прочитать файл");
            }
        }

    @Test
    void loadFromFileTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        fileBackedTaskManager.addTask(task1);
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание эпика 1");
        fileBackedTaskManager.addEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask1.getTaskId());
        fileBackedTaskManager.addSubtask(subtask1);
        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание эпика 2");
        fileBackedTaskManager.addEpicTask(epicTask2);
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", epicTask1.getTaskId());
        fileBackedTaskManager.addSubtask(subtask2);
        String expectedTask = "\uFEFF" + "id,type,name,status,description,epic" + fileBackedTaskManager.toString(task1)
                + fileBackedTaskManager.toString(epicTask1) + fileBackedTaskManager.toString(subtask1) +
                fileBackedTaskManager.toString(epicTask2) + fileBackedTaskManager.toString(subtask2);
        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(fileBackedTaskManager.getFile());
        try (FileReader fr = new FileReader(fileBackedTaskManager1.getFile(), StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(fr)) {
            String actualTask = "";
            while (br.ready()) {
                actualTask = actualTask + br.readLine();
            }
            assertEquals(expectedTask, actualTask, "Загрузка с файла некорректна");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException("Не получилось прочитать файл");
        }
    }

    @Test
    void loadFromFileCheckExceptionIfFileIsIncorrectTest() {
        File incorrecFile = null;
        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(incorrecFile);
        }, "Создание менеджера задач из некорректного файла должно приводить к исключению");
        assertEquals("Файл пустой.", exception.getMessage());
    }

    @Test
    void getFileBackedTest() {
        try {
            File file1 = File.createTempFile("class", ".csv");
            assertEquals(file1.getClass(), fileBackedTaskManager.getFile().getClass(), "Файл создаётся некорректно");
        } catch (IOException e) {
            throw new RuntimeException("Файл создаётся некорректно");
        }
    }

    @Test
    void testToStringTest() {
        fileBackedTaskManager.addTask(exampleTask);
        String expected = String.format("%s,%s,%s,%s,%s,,,,", exampleTask.getTaskId(), "TASK",
                exampleTask.getName(), exampleTask.getDescription(), exampleTask.getTaskStatus());
        String actual = fileBackedTaskManager.toString(exampleTask);
        assertEquals(expected, actual, "Строки из задач не совпадают");
    }

    @Test
    void testFromStringTest() {
        String taskInfo = "1,TASK,Задача 1,Описание задачи 1,NEW,,,,";
        fileBackedTaskManager.addTask(FileBackedTaskManager.fromString(taskInfo));
        assertEquals(exampleTask.getClass(), fileBackedTaskManager.getTaskById(1).getClass(), "Задачи не совпадают");
    }

    @Test
    void testTaskWithTimesToStringTest() {
        Task task1 = new Task("Задача 1","Описание задачи 1");
        fileBackedTaskManager.addTask(task1);
        LocalDateTime startTime = LocalDateTime.of(2025, 6, 5, 12, 0);
        Duration duration = Duration.ofMinutes(15);
        task1.setStartTime(startTime);
        task1.setDuration(duration);
        String expected = String.format("%s,%s,%s,%s,%s,05.06.2025 12:00,05.06.2025 12:15,15,", task1.getTaskId(), "TASK",
                task1.getName(), task1.getDescription(), task1.getTaskStatus());
        String actual = fileBackedTaskManager.toString(task1);
        assertEquals(expected, actual, "Строки из задач не совпадают");
    }

    @Test
    void testTaskWithTimesFromStringTest() {
        String taskInfo = "1,TASK,Задача 1,Описание задачи 1,NEW,05.06.2025 12:00,05.06.2025 12:15,15,";
        fileBackedTaskManager.addTask(FileBackedTaskManager.fromString(taskInfo));
        assertEquals(exampleTask.getClass(), fileBackedTaskManager.getTaskById(1).getClass(), "Задачи не совпадают");
        assertEquals("2025-06-05T12:00", fileBackedTaskManager.getTaskById(1).getStartTime().toString());
        assertEquals("2025-06-05T12:15", fileBackedTaskManager.getTaskById(1).getEndTime().toString());
        assertEquals("PT15M", fileBackedTaskManager.getTaskById(1).getDuration().toString());
    }

    @Test
    void creatingFileOnDirectoryResourcesTest() {
        FileBackedTaskManager fileBackedTaskManager1 = new FileBackedTaskManager();
        File dir = new File(fileBackedTaskManager1.getFile().getParentFile().toURI());
        assertEquals("resources", dir.getName());
    }
}