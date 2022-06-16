package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InvalidItemException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static long filmId = 1;

    @Override
    public Film createFilm(Film film) {
        createFilmValidation(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        createFilmValidation(film);
        updateFilmValidation(film);
        log.debug("Обновляемый фильм: {}", films.get(film.getId()));
        films.put(film.getId(), film);
        log.debug("Обновленный фильм: {}", film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    private void createFilmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка добавить фильм с пустым именем");
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Попытка добавить фильм с описанием более 200 символов");
            throw new ValidationException("Описание фильма не может быть больше 200 символов!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка добавить фильм с датой релиза раньше 28 декабря 1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895!");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.warn("Попытка добавить фильм с отрицательной или равной нулю продолжительностью");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом!");
        }
    }

    private void updateFilmValidation(Film film) {
        long id = film.getId();

        if (id == 0) {
            log.warn("Попытка обновить фильм без id");
            throw new ValidationException("У переданного фильма отсутствует id!");
        }
        if (id < 0) {
            log.warn("Попытка добавить фильм с отрицательным id = {}", id);
            throw new InvalidItemException("id не может быть отрицательным!");
        }
        if (!films.containsKey(id)) {
            log.warn("Попытка обновить фильм, отсутствующий в системе");
            throw new ValidationException("Фильма с id = " + id + " нет в системе!");
        }
    }
}
