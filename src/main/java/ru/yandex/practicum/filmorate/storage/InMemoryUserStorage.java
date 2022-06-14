package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InvalidItemException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static long userId = 1;


    @Override
    public User createUser(User user) {
        createUserValidation(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        createUserValidation(user);
        updateUserValidation(user);
        log.debug("Обновляемый пользователь: {}", users.get(user.getId()));
        users.put(user.getId(), user);
        log.debug("Обновленный пользователь: {}", user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private void createUserValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Попытка добавить пользователя с пустой электронной почтой");
            throw new ValidationException("Электронная почта не может быть пустой!");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Попытка добавить пользователя с электронной почтой {} без символа @", user.getEmail());
            throw new ValidationException("Электронная почта " + user.getEmail() + " должна содержать символ @!");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Попытка добавить пользователя с пустым логином");
            throw new ValidationException("Login не может быть пустым!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Попытка добавить пользователя с пустым именем!");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка добавить пользователя с датой рождения {} из будущего", user.getBirthday());
            throw new ValidationException("Дата рождения " + user.getBirthday() + " не может быть больше текущей " + LocalDate.now() + "!");
        }
    }

    private void updateUserValidation(User user) {
        long id = user.getId();

        if (id == 0) {
            log.warn("Попытка обновить пользователя без id");
            throw new ValidationException("У переданного пользователя отсутствует id!");
        }
        if (id < 0) {
            log.warn("Попытка добавить пользователя с отрицательным id = {}", id);
            throw new InvalidItemException("id не может быть отрицательным!");
        }
        if (!users.containsKey(id)) {
            log.warn("Попытка обновить пользователя, отсутствующего в системе");
            throw new ValidationException("Пользователя с id = " + id + " нет в системе!");
        }
    }
}
