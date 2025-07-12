package server;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer httpServer;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicTaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    static class Main {
        public static void main(String[] args) {
            try {
                HttpTaskServer taskServer = new HttpTaskServer(Managers.getDefault());
                taskServer.start();
                System.out.println("HTTP-сервер запущен на " + HttpTaskServer.PORT + " порту!");
            } catch (IOException e) {
                System.out.println("Ошибка запуска сервера.\n");
                e.printStackTrace();
            }
        }
    }
}




