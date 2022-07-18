package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private long userId = 1;


    @Override
    public long createUser(User user) {
        user.setId(userId++);
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь {}", user);
        return user.getId();
    }

    @Override
    public void updateUser(User user) {
        log.debug("Обновляемый пользователь: {}", users.get(user.getId()));
        users.put(user.getId(), user);
        log.debug("Обновленный пользователь: {}", user);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }
}
