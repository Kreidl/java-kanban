package server;

import com.google.gson.*;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import manager.TaskType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.handlers.adapters.DurationAdapter;
import server.handlers.adapters.LocalDateTimeAdapter;
import server.handlers.adapters.TaskStatusAdapter;

import java.io.IOException;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class HttpTaskServerTest {
    protected TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    GsonBuilder gsonBuilder = new GsonBuilder();
    protected final Gson gson = gsonBuilder.setPrettyPrinting().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(TaskType.class, new TaskStatusAdapter()).create();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void createHttpTaskServer() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpicTasks();
        httpTaskServer.start();
    }

    @AfterEach
    public void stopHttpTaskServer() {
        httpTaskServer.stop();
    }

}
