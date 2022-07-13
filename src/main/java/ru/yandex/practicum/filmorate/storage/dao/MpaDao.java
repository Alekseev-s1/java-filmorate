package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;
import java.util.Optional;

public interface MpaDao {
    List<MPARating> getAllMpa();
    Optional<MPARating> getMpaById(int mpaId);
}
