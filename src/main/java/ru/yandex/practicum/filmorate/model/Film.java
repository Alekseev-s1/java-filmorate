package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film {

    private long id;
    @NotBlank(message = "Значение параметра name должно быть заполнено")
    private String name;
    @Size(max = 200, message = "Значение параметра description не может превышать 200 символов")
    private String description;
    @NotNull(message = "Не задано значение параметра releaseDate")
    private LocalDate releaseDate;
    @Positive(message = "Значение параметра duration должно быть положительным числом")
    private long duration;
    @PositiveOrZero(message = "Значение параметра rate не может быть отрицательным")
    private int rate;
    private Set<Genre> genres;
    @NotNull(message = "Не задано значение параметра mpa")
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
