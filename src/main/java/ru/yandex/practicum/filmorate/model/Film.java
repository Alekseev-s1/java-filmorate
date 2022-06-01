package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.serializer.CustomDurationSerializer;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Film {

    private long id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    @JsonSerialize(using = CustomDurationSerializer.class)
    private final Duration duration;

    /*@Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration.toMinutes() +
                '}';
    }*/
}
