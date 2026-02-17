package models;

public enum Color {
    RED,
    BLACK,
    BLUE,
    YELLOW,
    BROWN;

    public static String list() {
        StringBuilder list = new StringBuilder();
        for (Color genre : Color.values()) {
            list.append(genre).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }

}
