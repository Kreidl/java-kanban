package server.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskIntersectWithOther;
import manager.TaskManager;
import manager.TaskType;
import server.exceptions.ElementNotFoundException;
import server.handlers.adapters.DurationAdapter;
import server.handlers.adapters.LocalDateTimeAdapter;
import server.handlers.adapters.TaskStatusAdapter;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.setPrettyPrinting().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(TaskType.class, new TaskStatusAdapter()).create();

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetTask(exchange);
                break;
            case "POST":
                handlePostTask(exchange);
                break;
            case "DELETE":
                handleDeleteTask(exchange);
                break;
            default:
                sendBadRequest(exchange, "Неизвестный метод запроса", 400);
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/tasks")) {
                if (taskManager.getAllTasks() == null) {
                    sendBadRequest(exchange, "Список задач пустой.", 404);
                }
                String[] tasks = new String[taskManager.getAllTasks().size()];
                for (int i = 0; i < taskManager.getAllTasks().size(); i++) {
                    tasks[i] = gson.toJson(taskManager.getAllTasks().get(i));
                }
                if (tasks.length == 0) {
                    sendBadRequest(exchange, "Список задач пустой.", 404);
                }
                sendText(exchange, gson.toJson(tasks));
            } else {
            Optional<String> idString = Optional.ofNullable(path.split("/")[2]);
            if (idString.isPresent()) {
                Task task;
                int id = Integer.parseInt(idString.get());
                task = taskManager.getTaskById(id);
                if (task == null) {
                    sendBadRequest(exchange, "Такая задача не существует.", 404);
                }
                sendText(exchange, gson.toJson(task));
            } else {
                sendBadRequest(exchange, "Такая задача не существует.", 404);
            }
        }
        } catch (ElementNotFoundException | NumberFormatException e) {
            sendBadRequest(exchange, "Во время выполнения запроса ресурса по URL-адресу: " + exchange.getRequestURI()
                    + ", произошла ошибка.\nПроверьте, пожалуйста, адрес и повторите попытку.", 404);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException, TaskIntersectWithOther {
        InputStream requestBody = exchange.getRequestBody();
        String jsonTask = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        if (jsonTask.isBlank()) {
            sendBadRequest(exchange, "Передана пустая задача.", 400);
        }
        try {
            Task taskFromJson = gson.fromJson(jsonTask, Task.class);
            var taskFindOpt = taskManager.getAllTasks().stream()
                    .map(Optional::of)
                    .filter(task -> Objects.equals(task.get().getTaskId(), taskFromJson.getTaskId()))
                    .findFirst();
            if (taskFindOpt.isEmpty()) {
                try {
                    taskManager.addTask(taskFromJson);
                    sendPostText(exchange, "Задача добавлена.");
                } catch (TaskIntersectWithOther e) {
                    sendBadRequest(exchange, e.getMessage(), 406);
                }
            } else {
                try {
                taskManager.updateTask(taskManager.getTaskById(taskFromJson.getTaskId()), taskFromJson);
                    sendPostText(exchange, "Задача обновлена.");
                } catch (TaskIntersectWithOther e) {
                    sendBadRequest(exchange, e.getMessage(), 406);
                }
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Передана некорректная задача", 404);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/tasks")) {
                if (taskManager.getAllTasks() == null) {
                    sendText(exchange, "Список задач уже пустой.");
                }
                for (Task task : taskManager.getAllTasks()) {
                    taskManager.deleteTaskById(task);
                }
                sendText(exchange, "Все задачи удалены.");
            } else {
                Optional<String> idString = Optional.ofNullable(path.split("/")[2]);
                if (idString.isPresent()) {
                    Task task;
                    int id = Integer.parseInt(idString.get());
                    task = taskManager.getTaskById(id);
                    if (task == null) {
                        sendBadRequest(exchange, "Такая задача не существует.", 404);
                    }
                    taskManager.deleteTaskById(task);
                    sendText(exchange, "Задача удалена.");
                } else {
                    sendBadRequest(exchange, "Такая задача не существует.", 404);
                }
            }
        } catch (ElementNotFoundException | NumberFormatException e) {
            sendBadRequest(exchange, "Во время выполнения запроса ресурса по URL-адресу: " + exchange.getRequestURI()
                    + ", произошла ошибка.\nПроверьте, пожалуйста, адрес и повторите попытку.", 404);
        }
    }
}
