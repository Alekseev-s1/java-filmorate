package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendshipDaoImpl implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES(?, ?)";

        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String deleteQuery = "DELETE FROM friends " +
                "WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(deleteQuery, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        String sqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE user_id IN (SELECT friend_id FROM friends f WHERE f.user_id = ?)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        String sqlQuery = "SELECT * " +
                "FROM users u " +
                "WHERE u.user_id IN (" +
                "SELECT u1.friend_id " +
                "FROM (" +
                "SELECT * " +
                "FROM friends " +
                "WHERE user_id = ?) AS u1 " +
                "JOIN (" +
                "SELECT * " +
                "FROM friends " +
                "WHERE user_id = ?) AS u2 " +
                "ON u1.friend_id = u2.friend_id)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherUserId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getTimestamp("birthday").toLocalDateTime().toLocalDate())
                .build();
    }
}
