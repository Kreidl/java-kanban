package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.TaskType;
import server.exceptions.ElementNotFoundException;
import server.handlers.adapters.DurationAdapter;
import server.handlers.adapters.LocalDateTimeAdapter;
import server.handlers.adapters.TaskStatusAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler {

    GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.setPrettyPrinting().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(TaskType.class, new TaskStatusAdapter()).create();

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetHistory(exchange);
                break;
            default:
                sendBadRequest(exchange, "METHOD_NOT_ALLOWED", 405);
        }
    }

    public void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/history")) {
                if (taskManager.getHistory() == null) {
                    sendBadRequest(exchange, "История задач пустая.", 404);
                }
                String[] tasks = new String[taskManager.getHistory().size()];
                for (int i = 0; i < taskManager.getHistory().size(); i++) {
                    tasks[i] = gson.toJson(taskManager.getHistory().get(i));
                }
                if (tasks.length == 0) {
                    sendBadRequest(exchange, "История задач пустая.", 404);
                }
                sendText(exchange, gson.toJson(tasks));
            } else {
                throw new IOException("Некорректный URI запроса");
            }
        } catch (ElementNotFoundException | NumberFormatException | IOException e) {
            sendBadRequest(exchange, "Во время выполнения запроса ресурса по URL-адресу: " + exchange.getRequestURI()
                    + ", произошла ошибка.\nПроверьте, пожалуйста, адрес и повторите попытку.", 400);
        }
    }
}