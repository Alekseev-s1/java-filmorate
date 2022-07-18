package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    long createFilm(Film film);

    void updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> getFilmById(long filmId);

    List<Film> getPopularFilms(int count);
}
