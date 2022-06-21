package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.serializer.CustomDurationSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film {

    private long id;
    @NotBlank(message = "Поле name должно быть заполнено")
    private String name;
    @Size(max = 200, message = "Значение поля description не может превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    @JsonSerialize(using = CustomDurationSerializer.class)
    private Duration duration;
    private int likesCount;
    private final Set<Long> likedUsersId = new HashSet<>();
}
