package com.lab6.common.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class MusicBand extends Element implements Serializable {
    @Serial
    private static final long serialVersionUID = 101L;
    private Long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name;//Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long numberOfParticipants; //Значение поля должно быть больше 0
    private Long singlesCount; //Значение поля должно быть больше 0
    private String description; //Поле может быть null
    private MusicGenre genre; //Поле может быть null
    private Person frontMan;
    private final String user;//Поле не может быть null


    @JsonCreator
    public MusicBand( @JsonProperty("id") Long id,
                      @JsonProperty("name") String name,
                      @JsonProperty("creationDate") LocalDate creationDate,
                      @JsonProperty("numberOfParticipants") Long numberOfParticipants,
                      @JsonProperty("description") String description,
                      @JsonProperty("coordinates") Coordinates coordinates,
                      @JsonProperty("singlesCount") Long singlesCount,
                      @JsonProperty("genre") MusicGenre genre,
                      @JsonProperty("frontMan") Person frontMan,
                      String user) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.numberOfParticipants = numberOfParticipants;
        this.description = description;
        this.coordinates = coordinates;
        this.singlesCount = singlesCount;
        this.genre = genre;
        this.frontMan = frontMan;
        this.user = user;

    }

    public void updateId(Long id) {
        this.id = id;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void updateNumberOfParticipants(Long numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public void updateSinglesCount(Long singlesCount) {
        this.singlesCount = singlesCount;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateGenre(MusicGenre genre) {
        this.genre = genre;
    }

    public void updateFrontMan(Person frontman) {
        this.frontMan = frontman;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public long getSinglesCount() {
        return singlesCount;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public Person getFrontMan() {
        return frontMan;
    }

    public String getDescription() {
        return description;
    }


    public String getUser() {
        return user;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MusicBand musicBand = (MusicBand) object;
        return Objects.equals(id, musicBand.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, numberOfParticipants, singlesCount, description, genre, frontMan);
    }

    @Override
    public String toString() {
        return String.format(
                "%-20s: %-30s \n" +
                        "%-20s: %-30s \n" +
                        "%-20s: %-30s\n" +
                        "%-20s: %-30s\n" +
                        "%-20s: %-30s\n" +
                        "%-20s: %-30s\n" +
                        "%-20s: %-30s\n" +
                        "%-20s: %-30s",
                "Название", name,
                "Координаты", coordinates,
                "Дата создания", creationDate,
                "Участников", numberOfParticipants,
                "Синглов", singlesCount,
                "Описание", description,
                "Жанр", genre,
                "Фронтмен",frontMan
        );
    }

    @Override
    public int compareTo(Element o) {
        return (int) (Math.sqrt(Math.pow(this.coordinates.getX(),2)+Math.pow(this.coordinates.getY(),2)) - Math.sqrt(Math.pow(o.getCoordinates().getX(),2)+Math.pow(o.getCoordinates().getY(),2)));
    }

    public boolean validate() {
        if (id == null || id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null) return false;
        if (numberOfParticipants <= 0) return false;
        if (singlesCount <= 0) return false;
        if (description == null) return false;
        if (frontMan == null || !frontMan.validate()) return false;
        return true;
    }

    public static String[] toArray(MusicBand band) {
        return new String[]{
                String.valueOf(band.getId()),
                band.getName(),
                Double.toString(band.getCoordinates().getX()),
                String.valueOf(band.getCoordinates().getY()),
                band.getCreationDate().toString(),
                String.valueOf(band.getNumberOfParticipants()),
                String.valueOf(band.getSinglesCount()),
                band.getDescription(),
                band.getGenre().toString(),
                band.getFrontMan().getName(),
                band.getFrontMan().getBirthday().toString(),
                band.getFrontMan().getEyeColor().toString()
        };
    }
}

