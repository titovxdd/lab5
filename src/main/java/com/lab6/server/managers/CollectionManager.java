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

    
    private final PriorityQueue<MusicBand> collection;

    
    private final Map<Long, MusicBand> bandsMap;

    private final DBManager dbManager;
    private LocalDateTime initializationDate;
    private LocalDateTime lastModificationDate;

    private CollectionManager() {
        
        this.collection = new PriorityQueue<>();
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

    public ExecutionStatus loadCollection() {
        synchronized (collection) {
            collection.clear();
            bandsMap.clear();

            ExecutionStatus status = dbManager.loadCollection(collection);
            if (status.isSuccess()) {
                
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


    public ExecutionStatus add(MusicBand band, Pair<String, String> user) {
        if (band == null || !band.validate()) {
            return new ExecutionStatus(false, "Invalid band data");
        }

        synchronized (collection) {
            try {
                ExecutionStatus dbStatus = dbManager.addMusicBand(band, user);
                if (!dbStatus.isSuccess()) {
                    return dbStatus;
                }
                
                long newId = Long.parseLong(dbStatus.getMessage());
                band.updateId(newId);
                
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

    
    public ExecutionStatus removeById(Long id, Pair<String, String> user) {
        if (id == null) {
            return new ExecutionStatus(false, "ID cannot be null");
        }
        synchronized (collection) {
            
            MusicBand band = bandsMap.get(id);
            if (band == null) {
                return new ExecutionStatus(false, "Band not found!");
            }

            ExecutionStatus dbStatus = dbManager.removeById(id, user);
            if (!dbStatus.isSuccess()) {
                return dbStatus;
            }

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

    public List<MusicBand> getBands() {
        synchronized (collection) {
            return new ArrayList<>(collection);
        }
    }

    
    public LocalDateTime getLastSaveDate() {
        synchronized (collection) {
            return lastModificationDate;
        }
    }

    
    public ExecutionStatus update(MusicBand band, Pair<String, String> user) {
        if (band == null || band.getId() == null) {
            return new ExecutionStatus(false, "Invalid band or missing ID");
        }

        synchronized (collection) {
            
            MusicBand existing = bandsMap.get(band.getId());
            if (existing == null) {
                return new ExecutionStatus(false, "Band not found!");
            }

            
            ExecutionStatus dbStatus = dbManager.updateMusicBand(band, user);
            if (!dbStatus.isSuccess()) {
                return dbStatus;
            }

            
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

    
    public ExecutionStatus clear(Pair<String, String> user) {
        synchronized (collection) {
            ExecutionStatus dbStatus = dbManager.clear(user);
            if (!dbStatus.isSuccess()) {
                return dbStatus;
            }

            String username = user.getFirst();

            
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


    

    
    public List<MusicBand> getCollection() {
        synchronized (collection) {
            return new ArrayList<>(collection);
        }
    }

    
    public MusicBand getById(Long id) {
        return bandsMap.get(id);
    }

    
    public boolean containsId(Long id) {
        return bandsMap.containsKey(id);
    }

    
    public int size() {
        synchronized (collection) {
            return collection.size();
        }
    }

    
    public boolean isEmpty() {
        synchronized (collection) {
            return collection.isEmpty();
        }
    }

    
    public MusicBand getFirst() {
        synchronized (collection) {
            return collection.peek();
        }
    }


    public LocalDateTime getInitializationDate() {
        return initializationDate;
    }

    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }
}