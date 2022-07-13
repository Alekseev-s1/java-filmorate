package ru.yandex.practicum.filmorate.storage.dao;

public interface FilmLikesDao {
    void addLike(long filmId, long userId);
    void removeLike(long filmId, long userId);
}
