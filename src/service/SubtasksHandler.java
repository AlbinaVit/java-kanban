package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = HttpTaskServer.getGson();

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetSubtasks(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetSubtaskById(exchange, Integer.parseInt(pathParts[2]));
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

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> tasks = taskManager.getAllSubtasks();
        if (tasks.isEmpty()) {
            sendNotFound(exchange);
        } else {
            String response = gson.toJson(tasks);
            sendText(exchange, response);
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {

        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask == null) {
            sendNotFound(exchange);
        } else {
            String response = gson.toJson(subtask);
            sendText(exchange, response);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        try {
            if (taskManager instanceof InMemoryTaskManager) {
                InMemoryTaskManager manager = (InMemoryTaskManager) taskManager;
                if (manager.checkForTimeIntersection(subtask)) {
                    sendHasInteractions(exchange);
                    return;
                }
            }
            if (subtask.getId() == 0) {
                Subtask createSubTask = taskManager.createSubtask(subtask);
                sendTextCreate(exchange, gson.toJson(Map.of("id", createSubTask.getId())));
            } else {
                taskManager.updateSubtask(subtask);
                sendText(exchange, gson.toJson(Map.of("id", subtask.getId())));
            }
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        taskManager.deleteSubtask(id);
        exchange.sendResponseHeaders(201, -1);
    }
}
