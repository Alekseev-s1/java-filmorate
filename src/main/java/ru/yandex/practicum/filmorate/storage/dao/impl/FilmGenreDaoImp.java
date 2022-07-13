package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmGenreDaoImp implements FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;

    @Autowired
    public FilmGenreDaoImp(JdbcTemplate jdbcTemplate, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
    }

    @Override
    public void updateFilmGenre(Film film) {
        List<Genre> dbGenres = genreDao.getFilmGenres(film.getId());
        List<Genre> filmGenres = film.getGenres();
        if (!dbGenres.equals(filmGenres) && filmGenres != null) {
            String deleteSqlQuery = "DELETE FROM film_genre " +
                    "WHERE film_id = ?";
            String createSqlQuery = "INSERT INTO film_genre (film_id, genre_id) " +
                    "VALUES(?, ?)";

            jdbcTemplate.update(deleteSqlQuery, film.getId());
            filmGenres.forEach(genre -> jdbcTemplate.update(createSqlQuery, film.getId(), genre.getId()));
        }
    }
}
