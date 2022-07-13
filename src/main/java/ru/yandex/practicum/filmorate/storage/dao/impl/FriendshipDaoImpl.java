package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

@Component
public class FriendshipDaoImpl implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id, friendship_state) " +
                "VALUES(?, ?, 'NOT_ACCEPTED')";

        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(User user, User friend) {
        String deleteQuery = "DELETE FROM friends " +
                "WHERE user_id = ? AND friend_id = ?";
        String updateQuery = "UPDATE friends " +
                "SET friendship_state = 'NOT_ACCEPTED'" +
                "WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(deleteQuery, user.getId(), friend.getId());
        jdbcTemplate.update(updateQuery, friend.getId(), user.getId());
    }
}
