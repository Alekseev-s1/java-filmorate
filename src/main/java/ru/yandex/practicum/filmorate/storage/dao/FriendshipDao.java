package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.User;

public interface FriendshipDao {
    void addFriend(User user, User friend);
    void removeFriend(User user, User friend);
}
