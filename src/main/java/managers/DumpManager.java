package managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.MusicBand;
import sup.Console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Scanner;

public class DumpManager {
    private final String fileName;
    private final Console console;

    public DumpManager(String fileName, Console console){
        this.fileName = fileName;
        this.console = console;
    }

    public void WriteCollection(PriorityQueue<MusicBand> collection){
        try {
            ObjectMapper mapper = new ObjectMapper();
            FileOutputStream writer = new FileOutputStream(fileName, true);
            for (MusicBand band : collection){
                writer.write(mapper.writeValueAsBytes(band));
            }
            writer.close();
        } catch (IOException e) {
            console.println("Произошла ошибка при записи коллекции в файл!");
        }
    }

    public void ReadCollection(PriorityQueue<MusicBand> collection){
        try (Scanner scanner = new Scanner(fileName)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
        }
    }
}
