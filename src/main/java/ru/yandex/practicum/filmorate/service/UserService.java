package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        userIdValidation(id);
        return userStorage.getUserById(id);
    }

    public User createUser(User user) {
        createUserValidation(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        createUserValidation(user);
        userIdValidation(user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        userIdValidation(userId);
        userIdValidation(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        userIdValidation(userId);
        userIdValidation(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(userId);
    }

    public List<User> getAllFriends(long userId) {
        userIdValidation(userId);

        return userStorage.getFriendsByUserId(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        userIdValidation(userId);
        userIdValidation(otherUserId);

        List<User> userFriends = userStorage.getFriendsByUserId(userId);
        List<User> otherUserFriends = userStorage.getFriendsByUserId(otherUserId);

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }

    private void userIdValidation(long id) {
        if (id <= 0) {
            throw new ItemNotFoundException("id не может быть меньше либо равно нулю!");
        }
        if (userStorage.getUserById(id) == null) {
            throw new ItemNotFoundException(String.format("Пользователь с id = %d не найден!", id));
        }
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
}
