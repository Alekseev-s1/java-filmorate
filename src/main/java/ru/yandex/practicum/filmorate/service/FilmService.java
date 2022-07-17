package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikesDao filmLikesDao;
    private final FilmGenreDao filmGenreDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmLikesDao filmLikesDao,
                       FilmGenreDao filmGenreDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikesDao = filmLikesDao;
        this.filmGenreDao = filmGenreDao;
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
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());

        filmGenreDao.updateFilmGenre(film);
        return filmStorage.updateFilm(film);
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        User user = getUserById(userId);

        filmLikesDao.addLike(film, user);
        filmStorage.updateFilm(film);

    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        User user = getUserById(userId);

        filmLikesDao.removeLike(film, user);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
