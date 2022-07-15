package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;

@Component
public class FilmLikesDaoImpl implements FilmLikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmLikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "INSERT INTO user_film_likes (user_id, film_id) " +
                "VALUES(?, ?)";
        int filmRate = film.getRate();
        film.setRate(++filmRate);

        jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        String sqlQuery = "DELETE FROM user_film_likes " +
                "WHERE user_id = ? AND film_id = ?";
        int filmRate = film.getRate();
        film.setRate(--filmRate);

        jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
    }
}
