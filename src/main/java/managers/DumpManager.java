package managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.MusicBand;
import sup.Console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
            FileOutputStream writer = new FileOutputStream(fileName);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            writer.write("[".getBytes());
            boolean first = true;
            for (MusicBand band : collection) {
                if (band.validate()) {
                    if (!first) {
                        writer.write(",".getBytes());
                    }
                    writer.write(mapper.writeValueAsBytes(band));
                    first = false;
                } else {
                    console.printError("Ошибка валидности элемента");
                }
            }
            writer.write("]".getBytes());
            writer.close();
        } catch (IOException e) {
            console.printError("Произошла ошибка при записи коллекции в файл");
        }
    }

    public void ReadCollection(PriorityQueue<MusicBand> collection){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            File file = new File(fileName);
            if (!file.exists()) {
                console.printError("Файл не существует: " + fileName);
                return;
            }
            MusicBand[] bands = mapper.readValue(file, MusicBand[].class);
            for (MusicBand band : bands) {
                if (band.validate()) {
                    collection.add(band);
                } else {
                    console.printError("Обнаружен некорректный элемент, id: " + band.getId());
                }
            }
        }  catch (JsonMappingException e) {
            console.printError("Ошибка структуры JSON. Возможно файл поврежден: " + e.getMessage());
        } catch (JsonProcessingException e) {
            console.printError("Ошибка обработки JSON: " + e.getMessage());
        } catch (IOException e) {
            console.printError("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            console.printError("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
