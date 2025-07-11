package server.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest extends HttpTaskServerTest {

    public SubtaskHandlerTest() throws IOException {
    }

    @Test
    public void addSubtaskTest() throws IOException, InterruptedException {
        //Проверяем добавление подзадачи без установленных времени и продолжительности выполнения
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epicTask.getTaskId());
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача 1", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
        subtask = taskManager.getAllSubtasks().getFirst();
        taskManager.deleteTaskById(subtask);

        //Проверяем добавление подзадачи с установленными временем и продолжительностью выполнения
        subtask.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        subtask.setDuration(Duration.ofMinutes(5));
        taskJson = gson.toJson(subtask);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        tasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача 1", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");

        //Проверяем добавление подзадачи с пересекающимся временем
        Subtask subtask2 = new Subtask("Задача 2", "Описание задачи 2", TaskStatus.NEW, epicTask.getTaskId());
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        subtask2.setDuration(Duration.ofMinutes(15));
        taskJson = gson.toJson(subtask2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals("Задача пересекается с другой.", response.body(), "Сообщение об ошибке некорректно");
        tasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Подзадача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        taskManager.deleteAllSubtasks();

        //Проверяем добавление подзадачи без указания id эпика
        String json = "{\"name\": \"Подзадача 1\", \"description\": \"Описание подзадачи 1\"}";
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        tasksFromManager= taskManager.getAllSubtasks();
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        //Проверяем обновление подзадачи
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epicTask.getTaskId());
        taskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("Обновлённая подзадача 1", "Обновлённое описание подзадачи 1", TaskStatus.NEW, epicTask.getTaskId());
        subtask1.setTaskId(subtask.getTaskId());
        String taskJson = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Обновлённая подзадача 1", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void getSubtaskTest() throws IOException, InterruptedException {
        //Проверка запроса GET на одну подзадачу
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        Subtask subtask = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", TaskStatus.NEW, epicTask.getTaskId());
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(5));
        taskManager.addSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String requestBody = response.body();
        Subtask subtask1 = gson.fromJson(requestBody, Subtask.class);
        assertEquals(subtask1, subtask, "Некорректная подзадача");

//      Проверка запроса GET на несколько подзадач
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", TaskStatus.NEW, epicTask.getTaskId());
        taskManager.addSubtask(subtask2);
        EpicTask epicTask1 = new EpicTask("Эпик 2", "Описание эпика 2");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", TaskStatus.NEW, epicTask1.getTaskId());
        taskManager.addSubtask(subtask3);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        requestBody = response.body();
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        Subtask subtask4 = null;
        Subtask subtask5 = null;
        Subtask subtask6 = null;
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<JsonElement> tasksJsonElements = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                tasksJsonElements.add(element);
            }
            subtask4 = gson.fromJson(tasksJsonElements.get(0).getAsString(), Subtask.class);
            subtask5 = gson.fromJson(tasksJsonElements.get(1).getAsString(), Subtask.class);
            subtask6 = gson.fromJson(tasksJsonElements.get(2).getAsString(), Subtask.class);
        }
        assertEquals(subtask1, subtask4, "Подзадачи не совпадают");
        assertEquals(subtask2, subtask5, "Подзадачи не совпадают");
        assertEquals(subtask3, subtask6, "Подзадачи не совпадают");
    }

    @Test
    public void deleteSubtaskTest() throws IOException, InterruptedException {
        //Удаление одной подзадачи
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        Subtask subtask = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", TaskStatus.NEW, epicTask.getTaskId());
        taskManager.addSubtask(subtask);
        assertNotNull(taskManager.getAllSubtasks(), "Подзадача не добавлена");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();
        assertEquals(0, tasksFromManager.size(), "Некорректное количество подзадач");

        //Удаление всех подзадач
        subtask.setTaskId(epicTask.getTaskId());
        taskManager.addSubtask(subtask);
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", TaskStatus.NEW, epicTask.getTaskId());
        taskManager.addSubtask(subtask3);
        EpicTask epicTask1 = new EpicTask("Эпик 2", "Описание эпика 2");
        taskManager.addEpicTask(epicTask1);
        Subtask subtask4 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", TaskStatus.NEW, epicTask1.getTaskId());
        taskManager.addSubtask(subtask4);
        tasksFromManager = taskManager.getAllSubtasks();
        assertEquals(3, tasksFromManager.size(), "Подзадачи не добавлены");
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = taskManager.getAllSubtasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasksFromManager.size(), "Некорректное количество подзадач");
    }

}