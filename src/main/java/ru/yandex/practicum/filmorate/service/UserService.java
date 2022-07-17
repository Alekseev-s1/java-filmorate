package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.util.List;

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
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        List<User> userFriends = friendshipDao.getFriends(userId);

        if (!userFriends.contains(friend)) {
            friendshipDao.addFriend(userId, friendId);
        }
    }

    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        List<User> friends = friendshipDao.getFriends(userId);

        if (friends.contains(friend)) {
            friendshipDao.removeFriend(userId, friendId);
        }
    }

    public List<User> getAllFriends(long userId) {
        getUserById(userId);
        return friendshipDao.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);

        return friendshipDao.getCommonFriends(userId, otherUserId);
    }
}
