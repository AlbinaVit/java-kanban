package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = HttpTaskServer.getGson();

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetTasks(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetTaskById(exchange, Integer.parseInt(pathParts[2]));
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, Integer.parseInt(pathParts[2]));
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        if (tasks.isEmpty()) {
            sendNotFound(exchange);
        } else {
            String response = gson.toJson(tasks);
            sendText(exchange, response);
        }
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task == null) {
            sendNotFound(exchange);
        } else {
            String response = gson.toJson(task);
            sendText(exchange, response);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (taskManager instanceof InMemoryTaskManager) {
            InMemoryTaskManager manager = (InMemoryTaskManager) taskManager;
            if (manager.checkForTimeIntersection(task)) {
                sendHasInteractions(exchange);
                return;
            }
        }
        taskManager.createTask(task);
        exchange.sendResponseHeaders(201, -1);
    }

    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        taskManager.deleteTask(id);
        exchange.sendResponseHeaders(201, -1);
    }
}
