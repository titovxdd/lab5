package com.lab6.common.models;

public abstract class Element implements Comparable<Element>{
    abstract public Long getId();
    abstract public Coordinates getCoordinates();
}
