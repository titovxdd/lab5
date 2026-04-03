package com.lab6.server.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.MusicBand;
import sup.Console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.PriorityQueue;

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
        } catch (AccessDeniedException e) {
            System.err.println("Ошибка доступа: Недостаточно прав для работы с файлом");
        } catch (NoSuchFileException e) {
        System.err.println("Файл не найден");
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
        } catch (NoSuchFileException e) {
            System.err.println("Файл не найден: " + e.getFile());
        } catch (AccessDeniedException e) {
            console.printError("Ошибка доступа: Недостаточно прав для работы с файлом");
        } catch (IOException e) {
            console.printError("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            console.printError(e.getMessage());
        }
    }
}
