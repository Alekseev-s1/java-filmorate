package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        idValidation(filmId);
        idValidation(userId);

        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikedUsers().contains(user)) {
            int likesCount = film.getLikesCount();
            film.setLikesCount(++likesCount);
            film.getLikedUsers().add(user);
        }
    }

    public void removeLike(long filmId, long userId) {
        idValidation(filmId);
        idValidation(userId);

        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);

        if (film.getLikedUsers().contains(user)) {
            int likesCount = film.getLikesCount();
            film.getLikedUsers().remove(user);
            film.setLikesCount(--likesCount);
        }
    }

    public List<Film> getTop10LikedFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        return allFilms.stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount))
                .limit(10)
                .collect(Collectors.toList());
    }

    private void idValidation(long id) {
        if (id <= 0) {
            throw new ValidationException("id не может быть меньше либо равно нулю!");
        }
    }
}
