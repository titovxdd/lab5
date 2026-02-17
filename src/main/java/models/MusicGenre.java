package models;

public enum MusicGenre {
    PSYCHEDELIC_ROCK,
    POP,
    MATH_ROCK,
    PUNK_ROCK;

    public static String list() {
        StringBuilder list = new StringBuilder();
        for (MusicGenre genre : MusicGenre.values()) {
            list.append(genre).append(", ");
        }
        return list.substring(0, list.length() - 2);
    }
}
