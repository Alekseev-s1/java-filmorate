package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;

@Component
public class FilmLikesDaoImpl implements FilmLikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmLikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQuery = "INSERT INTO user_film_likes (user_id, film_id) " +
                "VALUES(?, ?)";

        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQuery = "DELETE FROM user_film_likes " +
                "WHERE user_id = ? AND film_id = ?";

        jdbcTemplate.update(sqlQuery, userId, filmId);
    }
}
