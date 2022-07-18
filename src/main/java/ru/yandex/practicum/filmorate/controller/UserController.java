package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        createUpdateUserValidation(user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        createUpdateUserValidation(user);
        return userService.updateUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(
            @PathVariable long userId,
            @PathVariable long friendId
    ) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(
            @PathVariable long userId,
            @PathVariable long friendId
    ) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getAllFriends(@PathVariable long userId) {
        return userService.getAllFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable long userId,
            @PathVariable long otherId
    ) {
        return userService.getCommonFriends(userId, otherId);
    }

    private void createUpdateUserValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Попытка добавить пользователя с пустым именем!");
            user.setName(user.getLogin());
        }
    }
}
