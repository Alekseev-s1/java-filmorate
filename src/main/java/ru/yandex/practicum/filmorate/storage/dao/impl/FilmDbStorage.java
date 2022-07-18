package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long createFilm(Film film) {
        String filmSqlQuery = "INSERT INTO films(name, description, release_date, duration, likes_count, rating_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(filmSqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(film.getReleaseDate().atStartOfDay()));
            statement.setLong(4, film.getDuration());
            statement.setInt(5, film.getRate());
            statement.setInt(6, film.getMpa().getId());
            return statement;
        }, keyHolder);

        long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);
        return filmId;
    }

    @Override
    public void updateFilm(Film film) {
        String filmSqlQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, likes_count = ?, rating_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(filmSqlQuery,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * " +
                "FROM films f " +
                "JOIN mpa_rating mr ON f.rating_id = mr.rating_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        String sqlQuery = "SELECT * " +
                "FROM films f " +
                "JOIN mpa_rating mr ON f.rating_id = mr.rating_id " +
                "WHERE film_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT * " +
                "FROM films " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    public List<Genre> getGenresByFilm(long filmId) {
        String sqlQuery = "SELECT * " +
                "FROM genres g " +
                "WHERE g.genre_id IN (" +
                "SELECT fg.genre_id " +
                "FROM film_genre fg " +
                "WHERE film_id = ?)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getTimestamp("release_date").toLocalDateTime().toLocalDate())
                .duration(resultSet.getLong("duration"))
                .rate(resultSet.getInt("likes_count"))
                .mpa(MPARating.forValues(resultSet.getInt("rating_id")))
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();

        film.getGenres().addAll(getGenresByFilm(resultSet.getLong("film_id")));
        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("name"));
    }
}
