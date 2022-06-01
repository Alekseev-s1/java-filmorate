package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static long filmId = 1;

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        createFilmValidation(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        updateFilmValidation(film);
        log.debug("Обновляемый фильм: {}", film);
        films.put(film.getId(), film);
        log.debug("Обновленный фильм: {}", film);
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private void createFilmValidation(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка добавить фильм с пустым именем");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Попытка добавить фильм с описанием более 200 символов");
            throw new ValidationException("Описание фильма не может быть больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка добавить фильм с датой релиза раньше 28 декабря 1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.warn("Попытка добавить фильм с отрицательной или равной нулю продолжительностью");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private void updateFilmValidation(Film film) throws ValidationException {
        long id = film.getId();

        if (id == 0) {
            log.warn("Попытка обновить фильм без id");
            throw new ValidationException("У переданного фильма отсутствует id");
        }
        if (!films.containsKey(id)) {
            log.warn("Попытка обновить фильм, отсутствующий в системе");
            throw new ValidationException("Фильма с id = " + id + " нет в системе");
        }
    }
}
