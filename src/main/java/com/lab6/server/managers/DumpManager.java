package com.lab6.server.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lab6.server.Server;
import com.lab6.common.models.MusicBand;
import com.lab6.common.Sup.ExecutionStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.PriorityQueue;

public class DumpManager {
    private final String filePath;
    private static volatile DumpManager instance;

    private DumpManager() {
        this.filePath = System.getenv("LAB5_FILE_PATH");
        if (filePath == null) {
            Server.logger.severe("Environment variable LAB5_FILE_PATH not found!");
            System.exit(1);
        } else if (filePath.isEmpty()) {
            Server.logger.severe("Environment variable LAB5_FILE_PATH does not contain a file path!");
            System.exit(1);
        } else if (!filePath.endsWith(".json")) {
            Server.logger.severe("The file must be in .json format!");
            System.exit(1);
        } else if (!new File(filePath).exists()) {
            Server.logger.severe("The file at the specified path was not found!");
            System.exit(1);
        }
    }

    public static DumpManager getInstance() {
        if (instance == null) {
            synchronized (DumpManager.class) {
                if (instance == null) {
                    instance = new DumpManager();
                }
            }
        }
        return instance;
    }

    public ExecutionStatus WriteCollection(PriorityQueue<MusicBand> collection){
        try {
            FileOutputStream writer = new FileOutputStream(filePath);
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
                    return new ExecutionStatus(false, "Ошибка валидности элемента коллекции");
                }
            }
            writer.write("]".getBytes());
            writer.close();
            return new ExecutionStatus(true, "Коллекция успешно сохранена в файл");
        } catch (AccessDeniedException e) {
            return new ExecutionStatus(false, "Ошибка доступа: Недостаточно прав для работы с файлом");
        } catch (NoSuchFileException e) {
            return new ExecutionStatus(false, "Файл не найден");
        } catch (IOException e) {
            return new ExecutionStatus(false, "Произошла ошибка при записи коллекции в файл");
        }
    }

    public ExecutionStatus ReadCollection(PriorityQueue<MusicBand> collection){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            File file = new File(filePath);
            if (!file.exists()) {
                return new ExecutionStatus(false, "Файл не найден");
            }
            MusicBand[] bands = mapper.readValue(file, MusicBand[].class);
            for (MusicBand band : bands) {
                if (band.validate()) {
                    collection.add(band);
                } else {
                    return new ExecutionStatus(false,"Обнаружен некорректный элемент, id: " + band.getId());
                }
            }
            return new ExecutionStatus(true, "Коллекция успешно загружена");
        }  catch (JsonMappingException e) {
            return new ExecutionStatus(false,"Ошибка структуры JSON. Возможно файл поврежден: " + e.getMessage());
        } catch (JsonProcessingException e) {
            return new ExecutionStatus(false,"Ошибка обработки JSON: " + e.getMessage());
        } catch (NoSuchFileException e) {
            return new ExecutionStatus(false,"Файл не найден: " + e.getFile());
        } catch (AccessDeniedException e) {
            return new ExecutionStatus(false,"Ошибка доступа: Недостаточно прав для работы с файлом");
        } catch (IOException e) {
            return new ExecutionStatus(false,"Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            return new ExecutionStatus(false,e.getMessage());
        }
    }
}
