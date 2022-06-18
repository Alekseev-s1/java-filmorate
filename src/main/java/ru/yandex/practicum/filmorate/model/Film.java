package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.serializer.CustomDurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonSerialize(using = CustomDurationSerializer.class)
    private Duration duration;
    private int likesCount;
    private final Set<Long> likedUsersId = new HashSet<>();
}
