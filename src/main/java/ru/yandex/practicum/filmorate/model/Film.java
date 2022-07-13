package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.serializer.CustomDurationSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    @PositiveOrZero(message = "Значение поля rate не может быть отрицательным")
    private int rate;
    private List<Long> likedUsersId;
    private List<Genre> genres;
    private MPARating mpa;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
