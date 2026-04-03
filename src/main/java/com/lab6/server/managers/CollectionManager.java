package com.lab6.server.managers;

import com.lab6.common.models.MusicBand;
import com.lab6.common.Sup.ExecutionStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CollectionManager {
    private final DumpManager dumpManager = DumpManager.getInstance();
    private static volatile CollectionManager instance;
    private Long id = 1L;
    private PriorityQueue<MusicBand> collection = new PriorityQueue<>();
    private Map<Long, MusicBand>  bands = new HashMap<>();
    private LocalDateTime InitializationDate;
    private LocalDateTime lastSaveDate;

    private CollectionManager() {}


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

    public void sort() {
        PriorityQueue<MusicBand> sortedBands = new PriorityQueue<>();
        Map<Long, MusicBand> SortedBandsMap = new TreeMap<>(bands);
        sortedBands.addAll(SortedBandsMap.values());
        collection = sortedBands;
    }

    public PriorityQueue<MusicBand> getBands() {
        return collection;
    }

    public void removeById(Long elementId) {
        collection = collection.stream().filter(band -> !band.getId().equals(elementId)).collect(Collectors.toCollection(PriorityQueue::new));
    }

    public MusicBand getById(Long id) {
        return bands.get(id);
    }

    public ExecutionStatus loadCollection() {
        if (collection != null){
            collection.clear();
        }
        if (bands != null){
            bands.clear();
        }
        dumpManager.ReadCollection(collection);
        InitializationDate = LocalDateTime.now();
        lastSaveDate = LocalDateTime.now();
        for (MusicBand band : collection) {
            if (getById(band.getId()) != null) {
                return new ExecutionStatus(false, "Ошибка загрузки коллекции: обнаружены дубликаты id!");
            }
            bands.put(band.getId(), band);
        }
        return new ExecutionStatus(true, "Коллекция успешно загружена!");
    }
    public void saveCollection() {
        dumpManager.WriteCollection(collection);
        lastSaveDate = LocalDateTime.now();
    }


    public PriorityQueue<MusicBand> getCollection() {
        return collection;
    }

    public Long getFreeId() {
        while (bands.containsKey(id)) {
            id++;
        }
        return id;
    }
    public void clear() {
        if (collection != null){
            collection.clear();
        }
        bands.clear();
    }

    public boolean add(MusicBand band) {
        if ((band != null) && band.validate() && !bands.containsKey(band.getId())) {
            collection.add(band);
            bands.put(band.getId(), band);
            return true;
        } else {
            return false;
        }
    }

    public MusicBand head(){
        return collection.peek();
    }

    public LocalDateTime getInitializationDate() {
        return InitializationDate;
    }

    public LocalDateTime getLastSaveDate() {
        return lastSaveDate;
    }

}
