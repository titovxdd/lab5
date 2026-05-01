package com.lab6.server.managers;

import com.lab6.common.Sup.ExecutionStatus;
import com.lab6.common.Sup.Pair;
import com.lab6.common.models.MusicBand;
import com.lab6.server.Server;
import com.lab6.server.managers.DBManager;

import java.time.LocalDateTime;
import java.util.*;

public class CollectionManager {
    private static volatile CollectionManager instance;

    // Синхронизированная PriorityQueue (согласно требованию)
    private final PriorityQueue<MusicBand> collection;

    // Для быстрого поиска по id (тоже синхронизированный)
    private final Map<Long, MusicBand> bandsMap;

    private final DBManager dbManager;
    private LocalDateTime initializationDate;
    private LocalDateTime lastModificationDate;

    private CollectionManager() {
        // ✅ Используем synchronized коллекции (требование ЛР7)
        this.collection = new PriorityQueue<>(Comparator.comparing(MusicBand::getId));
        this.bandsMap = Collections.synchronizedMap(new HashMap<>());
        this.dbManager = DBManager.getInstance();
        this.initializationDate = LocalDateTime.now();
        this.lastModificationDate = LocalDateTime.now();
    }

    public static CollectionManager getInstance() {
        if (instance == null) {
            synchronized (CollectionManager.class) {
                if (instance == null) {
                    instance = new CollectionManager();
                }
            }
        }
        return instance;
    }

    // ==================== ЗАГРУЗКА ====================

    /**
     * Загружает коллекцию из БД при старте сервера.
     */
    public ExecutionStatus loadCollection() {
        synchronized (collection) {
            collection.clear();
            bandsMap.clear();

            ExecutionStatus status = dbManager.loadCollection(collection);
            if (status.isSuccess()) {
                // Заполняем Map для быстрого поиска
                for (MusicBand band : collection) {
                    bandsMap.put(band.getId(), band);
                }
                initializationDate = LocalDateTime.now();
                lastModificationDate = LocalDateTime.now();
                Server.logger.info("Collection loaded from DB. Size: " + collection.size());
            }
            return status;
        }
    }

    // ==================== ОПЕРАЦИИ МОДИФИКАЦИИ ====================

    /**
     * Добавление элемента.
     * Сначала в БД, потом в память.
     */
    public ExecutionStatus add(MusicBand band, Pair<String, String> user) {
        if (band == null || !band.validate()) {
            return new ExecutionStatus(false, "Invalid band data");
        }

        synchronized (collection) {
            try {
                // 1. Сохраняем в БД (получаем id)
                ExecutionStatus dbStatus = dbManager.addMusicBand(band, user);
                if (!dbStatus.isSuccess()) {
                    return dbStatus;
                }

                // 2. Обновляем id из БД
                long newId = Long.parseLong(dbStatus.getMessage());
                band.updateId(newId);

                // 3. Добавляем в память
                collection.add(band);
                bandsMap.put(band.getId(), band);
                lastModificationDate = LocalDateTime.now();

                Server.logger.info("Band added: " + band.getName() + " by " + user.getFirst());
                return new ExecutionStatus(true, "Band added with ID: " + newId);

            } catch (Exception e) {
                Server.logger.severe("Error adding band: " + e.getMessage());
                return new ExecutionStatus(false, "Error: " + e.getMessage());
            }
        }
    }

    /**
     * Удаление по id.
     * Только если пользователь владелец.
     */
    public ExecutionStatus removeById(Long id, Pair<String, String> user) {
        if (id == null) {
            return new ExecutionStatus(false, "ID cannot be null");
        }

        synchronized (collection) {
            // Проверяем, существует ли элемент
            MusicBand band = bandsMap.get(id);
            if (band == null) {
                return new ExecutionStatus(false, "Band not found!");
            }

            // Удаляем из БД
            ExecutionStatus dbStatus = dbManager.removeById(id, user);
            if (!dbStatus.isSuccess()) {
                return dbStatus;
            }

            // Удаляем из памяти
            collection.remove(band);
            bandsMap.remove(id);
            lastModificationDate = LocalDateTime.now();

            Server.logger.info("Band removed: " + id + " by " + user.getFirst());
            return new ExecutionStatus(true, "Band removed successfully!");
        }
    }

    public MusicBand head() {
        synchronized (collection) {
            return collection.peek();
        }
    }

    public PriorityQueue<MusicBand> getBands() {
        synchronized (collection) {
            return new PriorityQueue<>(collection);
        }
    }

    /**
     * Возвращает дату последнего сохранения коллекции.
     *
     * @return LocalDateTime дата последнего сохранения
     */
    public LocalDateTime getLastSaveDate() {
        synchronized (collection) {
            return lastModificationDate;
        }
    }

    /**
     * Обновление элемента.
     * Только если пользователь владелец.
     */
    public ExecutionStatus update(MusicBand band, Pair<String, String> user) {
        if (band == null || band.getId() == null) {
            return new ExecutionStatus(false, "Invalid band or missing ID");
        }

        synchronized (collection) {
            // Проверяем существование
            MusicBand existing = bandsMap.get(band.getId());
            if (existing == null) {
                return new ExecutionStatus(false, "Band not found!");
            }

            // Обновляем в БД
            ExecutionStatus dbStatus = dbManager.updateMusicBand(band, user);
            if (!dbStatus.isSuccess()) {
                return dbStatus;
            }

            // Обновляем в памяти (удаляем старый, добавляем обновлённый)
            collection.remove(existing);

            existing.updateName(band.getName());
            existing.updateCoordinates(band.getCoordinates());
            existing.updateNumberOfParticipants(band.getNumberOfParticipants());
            existing.updateSinglesCount(band.getSinglesCount());
            existing.updateDescription(band.getDescription());
            existing.updateGenre(band.getGenre());
            existing.updateFrontMan(band.getFrontMan());

            collection.add(existing);
            lastModificationDate = LocalDateTime.now();

            Server.logger.info("Band updated: " + band.getId() + " by " + user.getFirst());
            return new ExecutionStatus(true, "Band updated successfully!");
        }
    }

    /**
     * Очистка всех элементов пользователя.
     */
    public ExecutionStatus clear(Pair<String, String> user) {
        synchronized (collection) {
            ExecutionStatus dbStatus = dbManager.clear(user);
            if (!dbStatus.isSuccess()) {
                return dbStatus;
            }

            String username = user.getFirst();

            // Удаляем из памяти только элементы пользователя
            collection.removeIf(band -> {
                String owner = band.getUser();
                return owner != null && owner.equals(username);
            });

            bandsMap.entrySet().removeIf(entry -> {
                String owner = entry.getValue().getUser();
                return owner != null && owner.equals(username);
            });

            lastModificationDate = LocalDateTime.now();
            Server.logger.info("Cleared bands for user: " + username);
            return dbStatus;
        }
    }

    /**
     * Удаление первого элемента.
     */
    public ExecutionStatus removeFirst(Pair<String, String> user) {
        synchronized (collection) {
            if (collection.isEmpty()) {
                return new ExecutionStatus(false, "Collection is empty");
            }

            MusicBand first = collection.peek();
            return removeById(first.getId(), user);
        }
    }

    // ==================== ОПЕРАЦИИ ЧТЕНИЯ ====================

    /**
     * Возвращает копию коллекции (безопасно для многопоточности).
     */
    public PriorityQueue<MusicBand> getCollection() {
        synchronized (collection) {
            // Возвращаем копию, чтобы внешний код не модифицировал оригинал
            return new PriorityQueue<>(collection);
        }
    }

    /**
     * Поиск по id.
     */
    public MusicBand getById(Long id) {
        return bandsMap.get(id);
    }

    /**
     * Проверка существования элемента.
     */
    public boolean containsId(Long id) {
        return bandsMap.containsKey(id);
    }

    /**
     * Размер коллекции.
     */
    public int size() {
        synchronized (collection) {
            return collection.size();
        }
    }

    /**
     * Проверка, пуста ли коллекция.
     */
    public boolean isEmpty() {
        synchronized (collection) {
            return collection.isEmpty();
        }
    }

    /**
     * Получение первого элемента.
     */
    public MusicBand getFirst() {
        synchronized (collection) {
            return collection.peek();
        }
    }

    /**
     * Получение коллекции, отсортированной по местоположению.
     */
    public PriorityQueue<MusicBand> getSortedByLocation() {
        synchronized (collection) {
            PriorityQueue<MusicBand> sorted = new PriorityQueue<>(
                    (b1, b2) -> {
                        int xCompare = Double.compare(
                                b1.getCoordinates().getX(),
                                b2.getCoordinates().getX()
                        );
                        if (xCompare != 0) return xCompare;
                        return Float.compare(
                                b1.getCoordinates().getY(),
                                b2.getCoordinates().getY()
                        );
                    }
            );
            sorted.addAll(collection);
            return sorted;
        }
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    public LocalDateTime getInitializationDate() {
        return initializationDate;
    }

    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }
}