import managers.DumpManager;
import models.*;
import sup.Console;
import sup.FileConsole;
import sup.StandartConsole;

import java.time.LocalDate;
import java.util.PriorityQueue;

public class Main {
    public static void main(String[] args) {
        PriorityQueue<MusicBand> collection = new PriorityQueue<>();
        DumpManager manager = new DumpManager("collection.json", new StandartConsole());
        MusicBand band = new MusicBand(12L, "BI", LocalDate.parse("2000-10-10"), 3L, "FOFI", new Coordinates(12L, 3.0F), 12L, MusicGenre.POP, new Person("BOB", LocalDate.parse("1990-10-10"), Color.RED));
        manager.ReadCollection(collection);
        System.out.println(collection.toString());

    }
}