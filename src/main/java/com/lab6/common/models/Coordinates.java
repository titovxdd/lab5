package com.lab6.common.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Coordinates {
    private Long x;
    private float y;


    @JsonCreator
    public Coordinates(@JsonProperty("x") Long x, @JsonProperty("y") float  y){
        this.x = x;
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public long getX() {
        return x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return '{' +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public boolean validate(){
        return x <= 432 && x != null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Coordinates that = (Coordinates) object;
        return Double.compare(x, that.x) == 0 && Objects.equals(y, that.y);
    }
}
