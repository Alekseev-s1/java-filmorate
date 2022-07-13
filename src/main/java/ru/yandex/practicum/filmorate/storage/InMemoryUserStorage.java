package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private long userId = 1;


    @Override
    public User createUser(User user) {
        user.setId(userId++);
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Обновляемый пользователь: {}", users.get(user.getId()));
        users.put(user.getId(), user);
        log.debug("Обновленный пользователь: {}", user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getFriends(long userId) {
        List<Long> friendsId = users.get(userId).getFriendsId();
        List<User> friends = new ArrayList<>();
        friendsId.forEach(friendId -> friends.add(users.get(friendId)));
        return friends;
    }

    @Override
    public List<User> getLikedUsers(long filmId) {
        Film film = filmStorage.getFilmById(filmId).get();
        List<Long> usersId = film.getLikedUsersId();
        return usersId.stream()
                .map(id -> getUserById(id).get())
                .collect(Collectors.toList());
    }
}
