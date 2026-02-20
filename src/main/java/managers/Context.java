package managers;

import models.MusicBand;
import sup.Status;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class Context {
    private final DumpManager dumpManager;
    private Long id;
    private PriorityQueue<MusicBand> collection;
    private Map<Long, MusicBand> bands;
    private LocalDateTime InitializationDate;
    private LocalDateTime lastSaveDate;

    public Context(DumpManager dumpManager) {
        this.dumpManager = dumpManager;
    }

    public PriorityQueue<MusicBand> getBands() {
        return collection;
    }

    public void sort() {
        PriorityQueue<MusicBand> sortedBands = new PriorityQueue<>();
        Map<Long, MusicBand> SortedBandsMap = new TreeMap<>(bands);
        sortedBands.addAll(SortedBandsMap.values());
        collection = sortedBands;
    }

    public void removeById(Long elementId) {
        MusicBand band = bands.get(elementId);
        if (band != null) {
            collection.remove(band);
            bands.remove(elementId);
            id = elementId;
        }
    }

    public MusicBand getById(Long id) {
        return bands.get(id);
    }

    public Status loadCollection() {
        collection.clear();
        bands.clear();
        dumpManager.ReadCollection(collection);
        InitializationDate = LocalDateTime.now();
        lastSaveDate = LocalDateTime.now();
        for (MusicBand band : collection) {
            if (getById(band.getId()) != null) {
                return new Status(false, "Ошибка загрузки коллекции: обнаружены дубликаты id!");
            }
            bands.put(band.getId(), band);
        }
        return new Status(true, "Коллекция успешно загружена!");
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
        collection.clear();
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
