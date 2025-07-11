package server.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import tasks.EpicTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskHandlerTest extends HttpTaskServerTest {

    public EpicTaskHandlerTest() throws IOException {
    }

    @Test
    public void addEpicTaskTest() throws IOException, InterruptedException {
        //Проверяем добавление эпика без установленных времени и продолжительности выполнения
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        String taskJson = gson.toJson(epicTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<EpicTask> tasksFromManager = taskManager.getAllEpicTasks();
        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Эпик 1", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
        epicTask = taskManager.getAllEpicTasks().getFirst();
        taskManager.deleteEpicTaskById(epicTask);
    }

    @Test
    public void updateEpicTaskTest() throws IOException, InterruptedException {
        //Проверяем обновление
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        EpicTask epicTask1 = new EpicTask("Обновлённый эпик 1", "Обновлённое описание эпика 1");
        epicTask1.setTaskId(epicTask.getTaskId());
        String taskJson = gson.toJson(epicTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<EpicTask> tasksFromManager = taskManager.getAllEpicTasks();
        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Обновлённый эпик 1", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void getEpicTaskTest() throws IOException, InterruptedException {
        //Проверка запроса GET на один эпик
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String requestBody = response.body();
        EpicTask epicTask1 = gson.fromJson(requestBody, EpicTask.class);
        assertEquals(taskManager.getEpicTaskById(epicTask.getTaskId()), epicTask1, "Некорректный эпик");

        //Проверка запроса GET на несколько эпиков
        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание эпика 2");
        taskManager.addEpicTask(epicTask2);
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        requestBody = response.body();
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        EpicTask epicTask3 = null;
        Task epicTask4 = null;
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<JsonElement> tasksJsonElements = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                tasksJsonElements.add(element);
            }
            epicTask3 = gson.fromJson(tasksJsonElements.get(0).getAsString(), EpicTask.class);
            epicTask4 = gson.fromJson(tasksJsonElements.get(1).getAsString(), EpicTask.class);
        }
        assertEquals(epicTask1, epicTask3, "Эпики не совпадают");
        assertEquals(epicTask2, epicTask4, "Эпики не совпадают");
    }

    @Test
    public void deleteEpicTaskTest() throws IOException, InterruptedException {
        //Удаление одного эпика
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        assertNotNull(taskManager.getAllEpicTasks(), "Эпик не добавлен");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<EpicTask> epicTasksFromManager = taskManager.getAllEpicTasks();
        assertEquals(0, epicTasksFromManager.size(), "Некорректное количество задач");

        //Удаление всех эпиков
        epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        EpicTask epicTask1 = new EpicTask("Эпик 2", "Описание эпика 2");
        taskManager.addEpicTask(epicTask);
        taskManager.addEpicTask(epicTask1);
        epicTasksFromManager = taskManager.getAllEpicTasks();
        assertEquals(2, epicTasksFromManager.size(), "Эпики не добавлены");
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        epicTasksFromManager = taskManager.getAllEpicTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, epicTasksFromManager.size(), "Некорректное количество эпиков");
    }
}