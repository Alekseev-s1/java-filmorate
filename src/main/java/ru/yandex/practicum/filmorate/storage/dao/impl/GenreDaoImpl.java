package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * " +
                "FROM genres " +
                "ORDER BY genre_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenreById(int genreId) {
        String sqlQuery = "SELECT * " +
                "FROM genres " +
                "WHERE genre_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("name"));
    }
}
