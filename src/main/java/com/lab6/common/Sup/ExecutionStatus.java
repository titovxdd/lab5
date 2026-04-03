package com.lab6.common.Sup;

import com.lab6.common.models.MusicBand;

import java.util.PriorityQueue;

public class ExecutionStatus {
    private final boolean status;
    private String message;
    private PriorityQueue<MusicBand> collection;

    public ExecutionStatus(boolean success, PriorityQueue<MusicBand> collection) {
        this.status = success;
        this.collection = collection;
    }

    public ExecutionStatus(boolean success, String message) {
        this.status = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return status;
    }


    public PriorityQueue<MusicBand> getCollection() {
        return collection;
    }

    public String getMessage() {
        return message;
    }
}
