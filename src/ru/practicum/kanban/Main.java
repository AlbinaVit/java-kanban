package ru.practicum.kanban;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Переезд", "Организовать переезд на новую квартиру");
        Task task2 = new Task("Купить мебель", "Купить мебель для новой квартиры");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Организация праздника", "Организовать семейный праздник");
        Subtask subtask1 = new Subtask("Пригласить гостей", "Составить список гостей", epic1.getId());
        Subtask subtask2 = new Subtask("Заказать торт", "Выбрать торт и заказать его", epic1.getId());

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        System.out.println("Задачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " - " + task.getStatus()));

        System.out.println("Эпики:");
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " - " + epic.getStatus()));

        System.out.println("Подзадачи:");
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask.getName() + " - " + subtask.getStatus()));

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        System.out.println("Обновленные задачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " - " + task.getStatus()));
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " - " + epic.getStatus()));

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());

        System.out.println("После удаления:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " - " + task.getStatus()));
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " - " + epic.getStatus()));
    }
}
