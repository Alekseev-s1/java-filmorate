package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.List;

@Service
public class MpaService {

    private final MpaDao mpaDao;

    @Autowired
    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public List<MPARating> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    public MPARating getMpaById(int id) {
        return mpaDao.getMpaById(id)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("mpa с id = %d не найден", id)
                ));
    }
}
