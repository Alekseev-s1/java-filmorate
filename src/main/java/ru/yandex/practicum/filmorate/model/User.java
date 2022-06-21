package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {
    private long id;
    @NotBlank(message = "Электронная почта (email) обязательна для заполнения")
    @Email(message = "Неверный формат электронной почты (email)")
    private final String email;
    @NotBlank(message = "Поле login обязательно для заполнения")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения birthday не может быть больше текущей")
    private LocalDate birthday;
    private final Set<Long> friendsId = new HashSet<>();
}
