package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = HttpTaskServer.getGson();

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetAllEpics(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetEpicById(exchange, Integer.parseInt(pathParts[2]));
                    } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                        handleGetEpicSubtasks(exchange, Integer.parseInt(pathParts[2]));
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

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        if (epics.isEmpty()) {
            sendNotFound(exchange);
        } else {
            String response = gson.toJson(epics);
            sendText(exchange, response);
        }
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic == null) {
            sendNotFound(exchange);
        } else {
            String response = gson.toJson(epic);
            sendText(exchange, response);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int id) throws IOException {
        List<Subtask> subtasks = taskManager.getSubtaskByEpic(id);
        if (subtasks.isEmpty()) {
            sendNotFound(exchange);
        } else {
            String response = gson.toJson(subtasks);
            sendText(exchange, response);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        taskManager.createEpic(epic);
        exchange.sendResponseHeaders(201, -1);
    }

    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        taskManager.deleteEpic(id);
        exchange.sendResponseHeaders(200, -1);
    }
}
