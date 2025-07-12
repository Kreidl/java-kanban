package server.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import tasks.Task;
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

class TaskHandlerTest extends HttpTaskServerTest {

    public TaskHandlerTest() throws IOException {
    }

    @Test
    public void addTaskTest() throws IOException, InterruptedException {
        //Проверяем добавление задачи без установленных времени и продолжительности выполнения
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        task = taskManager.getAllTasks().getFirst();
        taskManager.deleteTaskById(task);

        //Проверяем добавление задачи с установленными временем и продолжительностью выполнения
        task.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        task.setDuration(Duration.ofMinutes(5));
        taskJson = gson.toJson(task);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        //Проверяем добавление задач с пересекающимся временем
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        task2.setDuration(Duration.ofMinutes(15));
        taskJson = gson.toJson(task2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals("Задача пересекается с другой.", response.body(), "Сообщение об ошибке некорректно");
        tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        taskManager.deleteAllTasks();

        //Проверяем добавление задачи только с названием и описанием задачи
        String json = "{\"name\": \"Задача 1\", \"description\": \"Описание задачи 1\"}";
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        tasksFromManager= taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        //Проверяем обновление задачи
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        taskManager.addTask(task);
        Task task1 = new Task("Обновлённая задача 1", "Обновлённое описание задачи 1", TaskStatus.NEW);
        task1.setTaskId(task.getTaskId());
        String taskJson = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Обновлённая задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void getTaskTest() throws IOException, InterruptedException {
        //Проверка запроса GET на одну задачу
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(5));
        taskManager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String requestBody = response.body();
        Task task1 = gson.fromJson(requestBody, Task.class);
        assertEquals(task, task1, "Некорректная задача");

//      Проверка запроса GET на несколько задач
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        taskManager.addTask(task2);
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        requestBody = response.body();
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        Task task3 = null;
        Task task4 = null;
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<JsonElement> tasksJsonElements = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                tasksJsonElements.add(element);
            }
            task3 = gson.fromJson(tasksJsonElements.get(0).getAsString(), Task.class);
            task4 = gson.fromJson(tasksJsonElements.get(1).getAsString(), Task.class);
        }
        assertEquals(task1, task3, "Задачи не совпадают");
        assertEquals(task2, task4, "Задачи не совпадают");
    }

    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        //Удаление одной задачи
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        taskManager.addTask(task);
        assertNotNull(taskManager.getAllTasks(), "Задача не добавлена");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

        //Удаление всех задач
        task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task1 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task1);
        tasksFromManager = taskManager.getAllTasks();
        assertEquals(2, tasksFromManager.size(), "Задачи не добавлены");
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        tasksFromManager = taskManager.getAllTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }
}