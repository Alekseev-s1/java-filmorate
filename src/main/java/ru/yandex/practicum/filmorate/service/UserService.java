package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        return userStorage.getUserById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Пользователь с id = %d не найден!", id)));
    }

    public User createUser(User user) {
        createUserValidation(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        createUserValidation(user);
        getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(userId);
    }

    public List<User> getAllFriends(long userId) {
        User user = getUserById(userId);
        return userStorage.getFriends(user);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        List<User> userFriends = userStorage.getFriends(user);
        List<User> otherUserFriends = userStorage.getFriends(otherUser);

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }

    private void createUserValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Попытка добавить пользователя с пустым именем!");
            user.setName(user.getLogin());
        }
    }
}
