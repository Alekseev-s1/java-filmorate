package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {

    private long id;
    @Email
    @NotNull
    @NotBlank
    private final String email;
    private final String login;
    private String name;
    private LocalDate birthday;
}
