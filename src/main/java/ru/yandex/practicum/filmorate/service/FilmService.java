package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Фильм с id = %d не найден!", id)));
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Пользователь с id = %d не найден!", id)));
    }

    public Film createFilm(Film film) {
        createFilmValidation(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        createFilmValidation(film);
        getFilmById(film.getId());
        return filmStorage.updateFilm(film);
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        getUserById(userId);

        if (!film.getLikedUsersId().contains(userId)) {
            int likesCount = film.getLikesCount();
            film.setLikesCount(++likesCount);
            film.getLikedUsersId().add(userId);
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        getUserById(userId);

        if (film.getLikedUsersId().contains(userId)) {
            int likesCount = film.getLikesCount();
            film.getLikedUsersId().remove(userId);
            film.setLikesCount(--likesCount);
        }
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = filmStorage.getAllFilms();
        return allFilms.stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
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
}
