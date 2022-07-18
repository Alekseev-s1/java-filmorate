package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPARating> getAllMpa() {
        String sqlQuery = "SELECT * " +
                "FROM mpa_rating";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Optional<MPARating> getMpaById(int mpaId) {
        String sqlQuery = "SELECT * " +
                "FROM mpa_rating " +
                "WHERE rating_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private MPARating mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return MPARating.forValues(resultSet.getInt("rating_id"));
    }
}
