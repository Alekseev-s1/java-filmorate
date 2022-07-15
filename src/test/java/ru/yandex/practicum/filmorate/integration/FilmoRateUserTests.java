package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmoRateUserTests {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;

    @Autowired
    public FilmoRateUserTests(@Qualifier("userDbStorage") UserStorage userStorage,
                              FriendshipDao friendshipDao,
                              JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void findUserById() {
        String createUserQuery = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Tom', 'tomLog', 'tom@test.com', '1999-01-01')";
        jdbcTemplate.update(createUserQuery);

        Optional<User> userOptional = userStorage.getUserById(1);

        assertThat(userOptional.isPresent(), is(true));
        User user = userOptional.get();
        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo("Tom"));
    }

    @Test
    public void findAllUsers() {
        String createUserQuery1 = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Tom', 'tomLog', 'tom@test.com', '1999-01-01')";
        String createUserQuery2 = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Den', 'denLog', 'den@test.com', '1998-01-01');";
        jdbcTemplate.update(createUserQuery1);
        jdbcTemplate.update(createUserQuery2);

        List<User> users = userStorage.getAllUsers();

        assertThat(users, hasSize(2));
        assertThat(users.get(0).getName(), equalTo("Tom"));
        assertThat(users.get(0).getId(), equalTo(1L));
        assertThat(users.get(1).getName(), equalTo("Den"));
        assertThat(users.get(1).getId(), equalTo(2L));
    }

    @Test
    public void createUserTest() {
        User user = User.builder()
                .name("John")
                .login("johnLog")
                .email("john@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User createdUser = userStorage.createUser(user);

        assertThat(createdUser.getId(), equalTo(1L));
        assertThat(createdUser.getName(), equalTo("John"));
    }

    @Test
    public void updateUserTest() {
        String createUserQuery = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Tom', 'tomLog', 'tom@test.com', '1999-01-01')";
        User user = User.builder()
                .id(1)
                .name("John")
                .login("johnLog")
                .email("john@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        jdbcTemplate.update(createUserQuery);

        User updatedUser = userStorage.updateUser(user);

        assertThat(updatedUser.getId(), equalTo(1L));
        assertThat(updatedUser.getName(), equalTo("John"));
    }

    @Test
    public void addFriendsTest() {
        String createUserQuery1 = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Tom', 'tomLog', 'tom@test.com', '1999-01-01')";
        String createUserQuery2 = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Den', 'denLog', 'den@test.com', '1998-01-01');";
        String createUserQuery3 = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Fred', 'fredLog', 'fred@test.com', '1997-01-01');";
        jdbcTemplate.update(createUserQuery1);
        jdbcTemplate.update(createUserQuery2);
        jdbcTemplate.update(createUserQuery3);

        friendshipDao.addFriend(1, 2);
        friendshipDao.addFriend(1, 3);

        List<User> friends = userStorage.getFriends(1);

        assertThat(friends, hasSize(2));
        assertThat(friends.get(0).getName(), equalTo("Den"));
        assertThat(friends.get(0).getId(), equalTo(2L));
        assertThat(friends.get(1).getName(), equalTo("Fred"));
        assertThat(friends.get(1).getId(), equalTo(3L));
    }

    @Test
    public void removeFriendsTest() {
        String createUserQuery1 = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Tom', 'tomLog', 'tom@test.com', '1999-01-01')";
        String createUserQuery2 = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Den', 'denLog', 'den@test.com', '1998-01-01');";
        String createUserQuery3 = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Fred', 'fredLog', 'fred@test.com', '1997-01-01');";
        String createFriendQuery1 = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES (1, 2)";
        String createFriendQuery2 = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES (1, 3)";
        jdbcTemplate.update(createUserQuery1);
        jdbcTemplate.update(createUserQuery2);
        jdbcTemplate.update(createUserQuery3);
        jdbcTemplate.update(createFriendQuery1);
        jdbcTemplate.update(createFriendQuery2);

        friendshipDao.removeFriend(1, 2);
        List<User> friends = userStorage.getFriends(1);

        assertThat(friends, hasSize(1));
        assertThat(friends.get(0).getName(), equalTo("Fred"));
        assertThat(friends.get(0).getId(), equalTo(3L));
    }
}
