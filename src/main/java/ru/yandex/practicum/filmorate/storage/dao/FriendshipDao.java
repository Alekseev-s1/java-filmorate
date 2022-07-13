package ru.yandex.practicum.filmorate.storage.dao;

public interface FriendshipDao {
    void addFriend(long userId, long friendId);
    void removeFriend(long userId, long friendId);
}
