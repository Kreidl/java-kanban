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
import tasks.EpicTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class EpicTaskHandler extends BaseHttpHandler {

    GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.setPrettyPrinting().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(TaskType.class, new TaskStatusAdapter()).create();

    public EpicTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetEpicTask(exchange);
                break;
            case "POST":
                handlePostEpicTask(exchange);
                break;
            case "DELETE":
                handleDeleteEpicTask(exchange);
                break;
            default:
                sendBadRequest(exchange, "Неизвестный метод запроса", 400);
        }
    }

    private void handleGetEpicTask(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/epics")) {
                if (taskManager.getAllEpicTasks() == null) {
                    sendBadRequest(exchange, "Список задач пустой.", 404);
                }
                String[] epicTasks = new String[taskManager.getAllEpicTasks().size()];
                for (int i = 0; i < taskManager.getAllEpicTasks().size(); i++) {
                    epicTasks[i] = gson.toJson(taskManager.getAllEpicTasks().get(i));
                }
                if (epicTasks.length == 0) {
                    sendBadRequest(exchange, "Список задач пустой.", 404);
                }
                sendText(exchange, gson.toJson(epicTasks));
            } else {
                Optional<String> idString = Optional.ofNullable(path.split("/")[2]);
                if (idString.isPresent()) {
                    EpicTask epicTask;
                    int id = Integer.parseInt(idString.get());
                    epicTask = taskManager.getEpicTaskById(id);
                    if (epicTask == null) {
                        sendBadRequest(exchange, "Такая задача не существует.", 404);
                    }
                    sendText(exchange, gson.toJson(epicTask));
                } else {
                    sendBadRequest(exchange, "Такая задача не существует.", 404);
                }
            }
        } catch (ElementNotFoundException | NumberFormatException e) {
            sendBadRequest(exchange, "Во время выполнения запроса ресурса по URL-адресу: " + exchange.getRequestURI()
                    + ", произошла ошибка.\nПроверьте, пожалуйста, адрес и повторите попытку.", 404);
        }
    }

    private void handlePostEpicTask(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String jsonTask = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        if (jsonTask.isBlank()) {
            sendBadRequest(exchange, "Передан пустой эпик.", 400);
        }
        try {
            EpicTask taskFromJson = gson.fromJson(jsonTask, EpicTask.class);
            var taskFindOpt = taskManager.getAllEpicTasks().stream()
                    .map(Optional::of)
                    .filter(task -> Objects.equals(task.get().getTaskId(), taskFromJson.getTaskId()))
                    .findFirst();
            if (taskFindOpt.isEmpty()) {
                taskManager.addEpicTask(taskFromJson);
                sendPostText(exchange, "Эпик добавлен.");
            } else {
                taskManager.updateEpicTask(taskManager.getEpicTaskById(taskFromJson.getTaskId()), taskFromJson);
                sendPostText(exchange, "Эпик обновлён.");
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Передан некорректный эпик", 404);
        } catch (TaskIntersectWithOther e) {
            sendBadRequest(exchange, e.getMessage(), 406);
        }

    }


    private void handleDeleteEpicTask(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/epics")) {
                if (taskManager.getAllEpicTasks() == null) {
                    sendText(exchange, "Список задач уже пустой.");
                }
                for (EpicTask epicTask : taskManager.getAllEpicTasks()) {
                    taskManager.deleteEpicTaskById(epicTask);
                }
                sendText(exchange, "Все задачи удалены.");
            } else {
                Optional<String> idString = Optional.ofNullable(path.split("/")[2]);
                if (idString.isPresent()) {
                    EpicTask epicTask;
                    int id = Integer.parseInt(idString.get());
                    epicTask = taskManager.getEpicTaskById(id);
                    if (epicTask == null) {
                        sendBadRequest(exchange, "Такая задача не существует.", 404);
                    }
                    taskManager.deleteEpicTaskById(epicTask);
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
