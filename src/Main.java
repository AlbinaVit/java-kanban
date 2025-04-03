import exseption.ManagerLoadExseption;
import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;
import utils.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException, ManagerLoadExseption {
        Path filePath = Files.createTempFile("test", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(filePath);

        initializeTasks(taskManager);
        displayAllTasks(taskManager);
        updateTasks(taskManager);
        displayUpdatedTasks(taskManager);
        deleteAllSubtasks(taskManager);
        deleteAllEpics(taskManager);
        deleteAllTasks(taskManager);
        Files.deleteIfExists(filePath);
    }

    private static void initializeTasks(FileBackedTaskManager taskManager) {
        Task task1 = new Task("Организация праздника", "Организовать переезд на новую квартиру", Status.NEW);
        Task task2 = new Task("Купить мебель", "Купить мебель для новой квартиры", Status.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Организация праздника", "Организовать семейный праздник");
        taskManager.createEpic(epic1);
        System.out.println("Создан эпик: " + epic1.getName());

        Subtask subtask1 = new Subtask("Пригласить гостей", "Составить список гостей", epic1.getId(), Status.NEW);
        Subtask subtask2 = new Subtask("Заказать торт", "Выбрать торт и заказать его", epic1.getId(), Status.NEW);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        System.out.println("Созданы подзадачи для эпика: " + epic1.getName());
    }

    private static void displayAllTasks(FileBackedTaskManager taskManager) {
        System.out.println("\nВсе задачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " - Статус " + task.getStatus()));

        System.out.println("\nВсе эпики:");
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " - Статус " + epic.getStatus()));

        System.out.println("\nВсе подзадачи:");
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask.getName() + " - Статус " + subtask.getStatus()));
    }

    private static void updateTasks(FileBackedTaskManager taskManager) {
        Task task1 = taskManager.getAllTasks().get(0); // Предполагаем, что первая задача это task1
        Subtask subtask1 = taskManager.getAllSubtasks().get(0); // Предполагаем, что первая подзадача это subtask1
        Subtask subtask2 = taskManager.getAllSubtasks().get(1); // Предполагаем, что вторая подзадача это subtask2

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        taskManager.updateEpic(taskManager.getAllEpics().get(0)); // Обновляем первый эпик
    }

    private static void displayUpdatedTasks(FileBackedTaskManager taskManager) {
        System.out.println("\nОбновленные задачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " -  Статус " + task.getStatus()));
        System.out.println("\nОбновленные сабтаски:");
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask.getName() + " -  Статус " + subtask.getStatus()));
        System.out.println("\nОбновленные эпики:");
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " -  Статус " + epic.getStatus()));
    }

    private static void deleteAllSubtasks(FileBackedTaskManager taskManager) {
        taskManager.deleteAllSubtasks();
        System.out.println("\nПосле удаления всех подзадач:");
        displayAllTasks(taskManager);
    }

    private static void deleteAllEpics(FileBackedTaskManager taskManager) {
        taskManager.deleteAllEpics();
        System.out.println("\nПосле удаления всех эпиков:");
        displayAllTasks(taskManager);
    }

    private static void deleteAllTasks(FileBackedTaskManager taskManager) {
        taskManager.deleteAllTasks();
        System.out.println("\nПосле удаления всех задач:");
        displayAllTasks(taskManager);
    }

}
