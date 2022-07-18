package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    long createUser(User user);

    void updateUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(long userId);
}
