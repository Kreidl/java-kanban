package server.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerSaveException;
import exceptions.TaskIntersectWithOther;
import manager.TaskManager;
import manager.TaskType;
import server.exceptions.ElementNotFoundException;
import server.handlers.adapters.DurationAdapter;
import server.handlers.adapters.LocalDateTimeAdapter;
import server.handlers.adapters.TaskStatusAdapter;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.setPrettyPrinting().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(TaskType.class, new TaskStatusAdapter()).create();

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetSubtask(exchange);
                break;
            case "POST":
                handlePostSubtask(exchange);
                break;
            case "DELETE":
                handleDeleteSubtask(exchange);
                break;
            default:
                sendBadRequest(exchange, "METHOD_NOT_ALLOWED", 405);
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/subtasks")) {
                if (taskManager.getAllSubtasks() == null) {
                    sendBadRequest(exchange, "Список подзадач пустой.", 404);
                }
                String[] tasks = new String[taskManager.getAllSubtasks().size()];
                for (int i = 0; i < taskManager.getAllSubtasks().size(); i++) {
                    tasks[i] = gson.toJson(taskManager.getAllSubtasks().get(i));
                }
                if (tasks.length == 0) {
                    sendBadRequest(exchange, "Список подзадач пустой.", 404);
                }
                sendText(exchange, gson.toJson(tasks));
            } else {
                Optional<String> idString = Optional.ofNullable(path.split("/")[2]);
                if (idString.isPresent()) {
                    Subtask subtask;
                    int id = Integer.parseInt(idString.get());
                    subtask = taskManager.getSubtaskById(id);
                    if (subtask == null) {
                        sendBadRequest(exchange, "Такая подзадача не существует.", 404);
                    }
                    sendText(exchange, gson.toJson(subtask));
                } else {
                    sendBadRequest(exchange, "Такая подзадача не существует.", 404);
                }
            }
        } catch (ElementNotFoundException | NumberFormatException e) {
            sendBadRequest(exchange, "Во время выполнения запроса ресурса по URL-адресу: " + exchange.getRequestURI()
                    + ", произошла ошибка.\nПроверьте, пожалуйста, адрес и повторите попытку.", 400);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String jsonTask = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        if (jsonTask.isBlank()) {
            sendBadRequest(exchange, "Передана пустая подзадача.", 400);
        }
        try {
            Subtask taskFromJson = gson.fromJson(jsonTask, Subtask.class);
            if (taskFromJson.getEpicId() == 0) {
                throw new ManagerSaveException("Подзадача с нулевым значением id эпика не может быть добавлена");
            }
            var taskFindOpt = taskManager.getAllSubtasks().stream()
                    .map(Optional::of)
                    .filter(task -> Objects.equals(task.get().getTaskId(), taskFromJson.getTaskId()))
                    .findFirst();
            if (taskFindOpt.isEmpty()) {
                taskManager.addSubtask(taskFromJson);
                sendPostText(exchange, "Подзадача добавлена.");
            } else {
                taskManager.updateSubtask(taskManager.getSubtaskById(taskFromJson.getTaskId()), taskFromJson);
                sendPostText(exchange, "Подзадача обновлена.");
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Передана некорректная подзадача", 400);
        } catch (TaskIntersectWithOther e) {
            sendBadRequest(exchange, e.getMessage(), 406);
        } catch (ManagerSaveException e) {
            sendBadRequest(exchange, "Передана подзадача без указания эпика.", 400);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/subtasks")) {
                if (taskManager.getAllSubtasks() == null) {
                    sendText(exchange, "Список подзадач уже пустой.");
                }
                for (Subtask task : taskManager.getAllSubtasks()) {
                    taskManager.deleteSubtaskById(task);
                }
                sendText(exchange, "Все подзадачи удалены.");
            } else {
                Optional<String> idString = Optional.ofNullable(path.split("/")[2]);
                if (idString.isPresent()) {
                    Subtask subtask;
                    int id = Integer.parseInt(idString.get());
                    subtask = taskManager.getSubtaskById(id);
                    if (subtask == null) {
                        sendBadRequest(exchange, "Такая подзадача не существует.", 404);
                    }
                    taskManager.deleteSubtaskById(subtask);
                    sendText(exchange, "Подзадача удалена.");
                } else {
                    sendBadRequest(exchange, "Такая подзадача не существует.", 404);
                }
            }
        } catch (ElementNotFoundException | NumberFormatException e) {
            sendBadRequest(exchange, "Во время выполнения запроса ресурса по URL-адресу: " + exchange.getRequestURI()
                    + ", произошла ошибка.\nПроверьте, пожалуйста, адрес и повторите попытку.", 400);
        }
    }
}
