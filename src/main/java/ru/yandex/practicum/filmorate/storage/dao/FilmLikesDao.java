package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface FilmLikesDao {
    void addLike(Film film, User user);
    void removeLike(Film film, User user);
}
