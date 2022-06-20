package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static long userId = 1;


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
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getFriends(User user) {
        Set<Long> friendsId = user.getFriendsId();
        List<User> friends = new ArrayList<>();
        friendsId.forEach(friendId -> friends.add(users.get(friendId)));
        return friends;
    }
}
