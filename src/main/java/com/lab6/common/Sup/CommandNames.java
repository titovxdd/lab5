package com.lab6.common.Sup;

public enum CommandNames {
    HELP("help", "вывести справку по доступным командам", PermissionType.DEFAULT),
    INFO("info", "вывести в стандартный поток вывода информацию о коллекции", PermissionType.DEFAULT),
    SHOW("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", PermissionType.DEFAULT),
    EXECUTE_SCRIPT("execute_script", "считать и исполнить скрипт из указанного файла", PermissionType.DEFAULT),
    EXIT("exit", "завершить программу (без сохранения в файл)", PermissionType.DEFAULT),
    ADD("add", "добавить новый элемент в коллекцию", PermissionType.USER),
    UPDATE("update", "обновить значение элемента коллекции, id которого равен заданному", PermissionType.USER),
    REMOVE_BY_ID("remove_by_id", "удалить элемент из коллекции по его id", PermissionType.USER),
    CLEAR("clear", "очистить коллекцию", PermissionType.USER),
    HEAD("head", "показать первый элемент из коллекции", PermissionType.USER),
    FILTER_GREATER_THAN_GENRE("filter_greater_than_genre", "вывести элементы, значение поля genre которых больше заданного", PermissionType.USER),
    ADD_IF_MIN("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции", PermissionType.USER),
    SORT("sort", "отсортировать коллекцию в естественном порядке", PermissionType.MODERATOR),
    UPDATE_USER_PERMISSION("update_user_permission", "обновить права пользователя в системе", PermissionType.ADMIN);

    private final Pair<String, String> commandDescription;
    private final PermissionType requiredPermission;

    CommandNames(String command, String description, PermissionType requiredPermission) {
        this.commandDescription = new Pair<>(command, description);
        this.requiredPermission = requiredPermission;
    }

    public String getName() {
        return commandDescription.getFirst();
    }

    public String getDescription() {
        return commandDescription.getSecond();
    }

    public PermissionType getRequiredPermission() {
        return requiredPermission;
    }
}
