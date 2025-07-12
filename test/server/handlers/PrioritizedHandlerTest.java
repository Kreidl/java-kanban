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
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest extends HttpTaskServerTest {

    public PrioritizedHandlerTest() throws IOException {
    }

    @Test
    public void getPrioritizedTest() throws IOException, InterruptedException {
        //Проверка GET-запроса на список приоритетных задач с несколькими задачами
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2025, 5, 6, 14, 0));
        task.setDuration(Duration.ofMinutes(15));
        taskManager.addTask(task);
        EpicTask epicTask = new EpicTask("Эпик 1", "Описание эпика 1");
        taskManager.addEpicTask(epicTask);
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask.getTaskId());
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 6, 15, 0));
        subtask1.setDuration(Duration.ofMinutes(15));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", epicTask.getTaskId());
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 6, 13, 0));
        subtask2.setDuration(Duration.ofMinutes(15));
        assertNotNull(taskManager.getPrioritizedTasks(), "Задачи не добавлены в список приоритета");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String requestBody = response.body();
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonArray jsonArray = null;
        if (jsonElement.isJsonArray()) {
            jsonArray = jsonElement.getAsJsonArray();
        }
        assertNotNull(jsonArray, "Список приоритетных задач вернулся пустой");
        assertEquals(taskManager.getPrioritizedTasks().size(), jsonArray.size(), "Количество задач в списке приоритетных задач некорректно");
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpicTasks();

        //Проверка GET-запроса на пустой список приоритетных задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Список приоритетных задач пустой.", response.body(), "Сообщение об ошибке некорректно");

        //Проверка GET-запроса на список приоритетных задач с некорректным URI
        url = URI.create("http://localhost:8080/prioritized/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("Во время выполнения запроса ресурса по URL-адресу: /prioritized/1, произошла ошибка." +
                "\nПроверьте, пожалуйста, адрес и повторите попытку.", response.body(), "Сообщение об ошибке некорректно");
    }

    @Test
    public void requestMethodMayBeOnlyGet() throws IOException, InterruptedException {
        //Проверка POST-запроса в историю задач
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("METHOD_NOT_ALLOWED", response.body(), "Сообщение об ошибке некорректно");
    }
}