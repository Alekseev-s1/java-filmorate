package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikesDao filmLikesDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmLikesDao filmLikesDao,
                       GenreDao genreDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikesDao = filmLikesDao;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Фильм с id = %d не найден!", filmId)));
    }

    private User getUserById(long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Пользователь с id = %d не найден!", userId)));
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
        User user = getUserById(userId);

        if (!film.getLikedUsersId().contains(userId)) {
            filmLikesDao.addLike(film, user);
            filmStorage.updateFilm(film);
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        User user = getUserById(userId);

        if (film.getLikedUsersId().contains(userId)) {
            filmLikesDao.removeLike(film, user);
            filmStorage.updateFilm(film);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void createFilmValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка добавить фильм с датой релиза раньше 28 декабря 1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.warn("Попытка добавить фильм с отрицательной или равной нулю продолжительностью");
            throw new ValidationException("Значение поля duration должно быть положительным числом");
        }
    }
}
