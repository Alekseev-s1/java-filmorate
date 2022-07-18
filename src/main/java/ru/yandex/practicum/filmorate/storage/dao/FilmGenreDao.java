package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreDao {
    void updateFilmGenre(Film film);

    List<Genre> getGenresByFilm(Film film);
}
