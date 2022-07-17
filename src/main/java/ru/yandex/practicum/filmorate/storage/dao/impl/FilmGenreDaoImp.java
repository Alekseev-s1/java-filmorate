package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Component
public class FilmGenreDaoImp implements FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateFilmGenre(Film film) {
        Set<Genre> filmGenres = film.getGenres();
        Set<Genre> dbFilmGenres = Set.copyOf(getGenresByFilm(film));

        if (filmGenres != null && !filmGenres.equals(dbFilmGenres)) {
            String deleteSqlQuery = "DELETE FROM film_genre " +
                    "WHERE film_id = ?";
            String createSqlQuery = "INSERT INTO film_genre (film_id, genre_id) " +
                    "VALUES(?, ?)";

            jdbcTemplate.update(deleteSqlQuery, film.getId());
            filmGenres.forEach(genre -> jdbcTemplate.update(createSqlQuery, film.getId(), genre.getId()));
        }

    }

    @Override
    public List<Genre> getGenresByFilm(Film film) {
        String sqlQuery = "SELECT * " +
                "FROM genres g " +
                "WHERE g.genre_id IN (" +
                "SELECT fg.genre_id " +
                "FROM film_genre fg " +
                "WHERE fg.film_id = ?)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, film.getId());
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("name"));
    }
}
