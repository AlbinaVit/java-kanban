public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

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

        System.out.println("\nВсе задачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " - Статус " + task.getStatus()));

        System.out.println("\nВсе эпики:");
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " - Статус " + epic.getStatus()));

        System.out.println("\nВсе подзадачи:");
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask.getName() + " - Статус " + subtask.getStatus()));

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        taskManager.updateEpic(epic1);

        System.out.println("\nОбновленные задачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " -  Статус " + task.getStatus()));
        System.out.println("\nОбновленные сабтаски:");
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask.getName() + " -  Статус " + subtask.getStatus()));
        System.out.println("\nОбновленные эпики:");
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " -  Статус " + epic.getStatus()));

        taskManager.deleteAllSubtasks();
        System.out.println("\nПосле удаления всех подзадач:");
        System.out.println("\nЭпики:");
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " -  Статус " + epic.getStatus()));
        System.out.println("\nПодзадачи:");
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask.getName() + " -  Статус " + subtask.getStatus()));
        System.out.println("\nЗадачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " -  Статус " + task.getStatus()));

        taskManager.deleteAllEpics();
        System.out.println("\nПосле удаления всех эпиков:");
        System.out.println("\nЭпики:");
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " -  Статус " + epic.getStatus()));
        System.out.println("\nПодзадачи:");
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask.getName() + " -  Статус " + subtask.getStatus()));
        System.out.println("\nЗадачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " -  Статус " + task.getStatus()));

        taskManager.deleteAllTasks();
        System.out.println("\nПосле удаления всех задач:");
        System.out.println("\nЭпики:");
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic.getName() + " -  Статус " + epic.getStatus()));
        System.out.println("\nПодзадачи:");
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask.getName() + " -  Статус " + subtask.getStatus()));
        System.out.println("\nЗадачи:");
        taskManager.getAllTasks().forEach(task -> System.out.println(task.getName() + " -  Статус " + task.getStatus()));
    }
}
