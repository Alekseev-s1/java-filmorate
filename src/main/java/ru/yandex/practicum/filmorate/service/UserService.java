package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        idValidation(userId);
        idValidation(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friend);
        friend.getFriends().add(user);
    }

    public void removeFriend(long userId, long friendId) {
        idValidation(userId);
        idValidation(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
    }

    public Set<User> getAllFriends(long userId) {
        idValidation(userId);

        return userStorage.getUserById(userId).getFriends();
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        idValidation(userId);
        idValidation(otherUserId);

        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        Set<User> userFriends = user.getFriends();
        Set<User> otherUserFriends = otherUser.getFriends();

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }

    private void idValidation(long id) {
        if (id <= 0) {
            throw new ValidationException("id пользователя не может быть меньше либо равно нулю!");
        }
    }
}
