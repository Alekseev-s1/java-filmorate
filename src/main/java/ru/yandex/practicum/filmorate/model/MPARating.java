package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MPARating {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    private final int id;
    @NotBlank(message = "Значение параметра name должно быть заполнено")
    private final String name;

    MPARating(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonCreator
    public static MPARating forValues(@JsonProperty("id") int id) {
        for (MPARating rating : values()) {
            if (rating.id == id) {
                return rating;
            }
        }

        return null;
    }
}
