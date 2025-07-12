package server.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import server.HttpTaskServerTest;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest extends HttpTaskServerTest {

    public HistoryHandlerTest() throws IOException {
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        //Проверка GET-запроса на историю задач с несколькими задачами
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        taskManager.addTask(task);
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        Subtask subtask = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask.getTaskId());
        taskManager.addSubtask(subtask);
        List<Task> tasks = new ArrayList<>();
        tasks.add(taskManager.getTaskById(task.getTaskId()));
        tasks.add(taskManager.getEpicTaskById(epicTask.getTaskId()));
        tasks.add(taskManager.getSubtaskById(subtask.getTaskId()));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String requestBody = response.body();
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonArray jsonArray = null;
        if (jsonElement.isJsonArray()) {
            jsonArray = jsonElement.getAsJsonArray();
        }
        assertNotNull(jsonArray, "История задач вернулась пустой");
        assertEquals(tasks.size(), jsonArray.size(), "Количество задач в истории задач некорректно");
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpicTasks();

        //Проверка GET-запроса на пустую историю задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("История задач пустая.", response.body());

        //Проверка GET-запроса на список приоритетных задач с некорректным URI
        url = URI.create("http://localhost:8080/history/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("Во время выполнения запроса ресурса по URL-адресу: /history/1, произошла ошибка." +
                "\nПроверьте, пожалуйста, адрес и повторите попытку.", response.body(), "Сообщение об ошибке некорректно");
    }

    @Test
    public void requestMethodMayBeOnlyGet() throws IOException, InterruptedException {
        //Проверка POST-запроса в историю задач
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("METHOD_NOT_ALLOWED", response.body(), "Сообщение об ошибке некорректно");
    }
}