package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmLikesDaoImpl implements FilmLikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmLikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Film film, User user) {
        List<User> users = getLikedUsers(film.getId());

        if (!users.contains(user)) {
            String sqlQuery = "INSERT INTO user_film_likes (user_id, film_id) " +
                    "VALUES(?, ?)";

            int filmRate = film.getRate();
            film.setRate(++filmRate);
            jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
        }
    }

    @Override
    public void removeLike(Film film, User user) {
        List<User> users = getLikedUsers(film.getId());

        if (users.contains(user)) {
            String sqlQuery = "DELETE FROM user_film_likes " +
                    "WHERE user_id = ? AND film_id = ?";

            int filmRate = film.getRate();
            film.setRate(--filmRate);
            jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
        }
    }

    @Override
    public List<User> getLikedUsers(long film_id) {
        String sqlQuery = "SELECT * " +
                "FROM users u " +
                "WHERE u.user_id IN (" +
                "SELECT ufl.user_id " +
                "FROM user_film_likes ufl " +
                "WHERE film_id = ?)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, film_id);
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
