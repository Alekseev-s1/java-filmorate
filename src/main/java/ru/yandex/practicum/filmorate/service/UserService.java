package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Пользователь с id = %d не найден!", userId)));
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

        if (!user.getFriendsId().contains(friendId) && !friend.getFriendsId().contains(userId)) {
            friendshipDao.addFriend(userId, friendId);
        }
    }

    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        getUserById(friendId);

        if (user.getFriendsId().contains(friendId)) {
            friendshipDao.removeFriend(userId, friendId);
        }
    }

    public List<User> getAllFriends(long userId) {
        getUserById(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);

        List<User> userFriends = userStorage.getFriends(userId);
        List<User> otherUserFriends = userStorage.getFriends(otherUserId);

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
