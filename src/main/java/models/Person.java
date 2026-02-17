package models;
import java.time.LocalDate;
import java.util.Objects;

public class Person {
    private String name;
    private LocalDate birthday;
    private Color eyeColor;

    public Person(String name, LocalDate birthday, Color eyeColor){
        this.name = name;
        this.birthday = birthday;
        this.eyeColor = eyeColor;
    }

    public String getName() {
        return name;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
        return '{' +
                "name='" + name + '\'' +
                ", birthdate='" + birthday + '\'' +
                ", eyeColor= '" + eyeColor + '\'' +
                '}';
    }

    public boolean validate() {
        return name != null && eyeColor != null && birthday != null && !(name.isEmpty());
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, birthday, eyeColor);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Person studio = (Person) object;
        return Objects.equals(name, studio.name) && Objects.equals(birthday, studio.birthday) && Objects.equals(eyeColor, studio.eyeColor);
    }
}
