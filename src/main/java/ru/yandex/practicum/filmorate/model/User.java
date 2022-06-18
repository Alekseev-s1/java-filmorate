package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {

    private long id;
    private final String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private final Set<Long> friendsId = new HashSet<>();
}
