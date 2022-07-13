package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final GenreDao genreDao;
    private final FilmGenreDao filmGenreDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         GenreDao genreDao,
                         FilmGenreDao filmGenreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.genreDao = genreDao;
        this.filmGenreDao = filmGenreDao;
    }

    @Override
    public Film createFilm(Film film) {
        String filmSqlQuery = "INSERT INTO films(name, description, release_date, duration, likes_count, rating_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        String genreSqlQuery = "INSERT INTO film_genre(film_id, genre_id) " +
                "VALUES(?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(filmSqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(film.getReleaseDate().atStartOfDay()));
            statement.setLong(4, film.getDuration().toSeconds());
            statement.setInt(5, film.getRate());
            statement.setInt(6, film.getMpa().getId());
            return statement;
        }, keyHolder);

        long filmId = keyHolder.getKey().longValue();

        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> jdbcTemplate.update(genreSqlQuery, filmId, genre.getId()));
        }

        return getFilmById(filmId).get();
    }

    @Override
    public Film updateFilm(Film film) {
        String filmSqlQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, likes_count = ?, rating_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(filmSqlQuery,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration().toSeconds(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        filmGenreDao.updateFilmGenre(film);

        return getFilmById(film.getId()).get();
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * " +
                "FROM films ";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        String sqlQuery = "SELECT * " +
                "FROM films " +
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

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getTimestamp("release_date").toLocalDateTime().toLocalDate())
                .duration(Duration.ofSeconds(resultSet.getInt("duration")))
                .rate(resultSet.getInt("likes_count"))
                .mpa(MPARating.forValues(resultSet.getInt("rating_id")))
                .likedUsersId(userStorage.getLikedUsers(resultSet.getLong("film_id")).stream()
                        .map(User::getId)
                        .collect(Collectors.toList()))
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();

        film.getGenres().addAll(genreDao.getFilmGenres(resultSet.getLong("film_id")));
        return film;
    }
}
