package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {

    private long id;
    private final String email;
    private final String login;
    private String name;
    private LocalDate birthday;
    private Set<User> friends;
}
