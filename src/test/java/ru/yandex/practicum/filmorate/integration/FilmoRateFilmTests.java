package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmoRateFilmTests {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikesDao filmLikesDao;

    @Autowired
    public FilmoRateFilmTests(JdbcTemplate jdbcTemplate,
                              @Qualifier("filmDbStorage") FilmStorage filmStorage,
                              @Qualifier("userDbStorage") UserStorage userStorage,
                              FilmLikesDao filmLikesDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikesDao = filmLikesDao;
    }

    @Test
    public void getFilmByIdTest() {
        String createFilmQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES ('Senna', 'Some description1', '2010-09-07', '107', 3);";
        jdbcTemplate.update(createFilmQuery);

        Optional<Film> filmOptional = filmStorage.getFilmById(1);
        assertThat(filmOptional.isPresent(), is(true));
        Film film = filmOptional.get();
        assertThat(film.getId(), equalTo(1L));
        assertThat(film.getName(), equalTo("Senna"));
    }

    @Test
    public void createFilmTest() {
        Film film = Film.builder()
                .id(1)
                .name("Senna")
                .description("Some description")
                .releaseDate(LocalDate.of(2010, 11, 11))
                .duration(100)
                .rate(5)
                .mpa(MPARating.G)
                .build();

        Film createdFilm = filmStorage.createFilm(film);
        assertThat(createdFilm.getId(), equalTo(1L));
        assertThat(createdFilm.getName(), equalTo("Senna"));
    }

    @Test
    public void updateFilmTest() {
        String createFilmQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES ('Senna', 'Some description1', '2010-09-07', '107', 3);";
        Film film = Film.builder()
                .id(1)
                .name("Home alone")
                .description("Some description1")
                .releaseDate(LocalDate.of(1990, 11, 11))
                .duration(101)
                .rate(4)
                .mpa(MPARating.NC_17)
                .build();
        jdbcTemplate.update(createFilmQuery);

        Film updatedFilm = filmStorage.updateFilm(film);
        assertThat(updatedFilm.getId(), equalTo(1L));
        assertThat(updatedFilm.getName(), equalTo("Home alone"));
    }

    @Test
    public void getAllFilmsTest() {
        String createFilmQuery1 = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES ('Senna', 'Some description1', '2010-09-07', '107', 3);";
        String createFilmQuery2 = "INSERT INTO films (name, description, release_date, duration, likes_count, rating_id) " +
                "VALUES ('Lock, Stock and Two Smoking Barrels', 'Some description2', '1998-08-23', '107', '1', 2);";
        jdbcTemplate.update(createFilmQuery1);
        jdbcTemplate.update(createFilmQuery2);

        List<Film> films = filmStorage.getAllFilms();
        assertThat(films.get(0).getId(), equalTo(1L));
        assertThat(films.get(0).getName(), equalTo("Senna"));
        assertThat(films.get(1).getId(), equalTo(2L));
        assertThat(films.get(1).getName(), equalTo("Lock, Stock and Two Smoking Barrels"));
    }

    @Test
    public void getPopularFilms() {
        String createFilmQuery1 = "INSERT INTO films (name, description, release_date, duration, likes_count, rating_id) " +
                "VALUES ('Senna', 'Some description1', '2010-09-07', '107', '3', 3);";
        String createFilmQuery2 = "INSERT INTO films (name, description, release_date, duration, likes_count, rating_id) " +
                "VALUES ('Lock, Stock and Two Smoking Barrels', 'Some description2', '1998-08-23', '107', '7', 2);";
        jdbcTemplate.update(createFilmQuery1);
        jdbcTemplate.update(createFilmQuery2);

        List<Film> films = filmStorage.getPopularFilms(2);
        assertThat(films.get(0).getId(), equalTo(2L));
        assertThat(films.get(0).getName(), equalTo("Lock, Stock and Two Smoking Barrels"));
        assertThat(films.get(1).getId(), equalTo(1L));
        assertThat(films.get(1).getName(), equalTo("Senna"));
    }

    @Test
    public void addLikeTest() {
        String createFilmQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES ('Senna', 'Some description1', '2010-09-07', '107', 3);";
        String createUserQuery = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Tom', 'tomLog', 'tom@test.com', '1999-01-01')";
        jdbcTemplate.update(createFilmQuery);
        jdbcTemplate.update(createUserQuery);

        Film film = filmStorage.getFilmById(1).get();
        User user = userStorage.getUserById(1).get();
        filmLikesDao.addLike(film, user);

        List<User> likedUsersId = filmLikesDao.getLikedUsers(1);
        assertThat(likedUsersId.get(0).getId(), equalTo(1L));
    }

    @Test
    public void removeLikeTest() {
        String createFilmQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES ('Senna', 'Some description1', '2010-09-07', '107', 3);";
        String createUserQuery = "INSERT INTO users (name, login, email, birthday) " +
                "VALUES ('Tom', 'tomLog', 'tom@test.com', '1999-01-01')";
        String createLikeQuery = "INSERT INTO user_film_likes (user_id, film_id) " +
                "VALUES (1, 1);";
        jdbcTemplate.update(createFilmQuery);
        jdbcTemplate.update(createUserQuery);
        jdbcTemplate.update(createLikeQuery);

        Film film = filmStorage.getFilmById(1).get();
        User user = userStorage.getUserById(1).get();
        filmLikesDao.removeLike(film, user);

        List<User> likedUsersId = filmLikesDao.getLikedUsers(1);
        assertThat(likedUsersId, hasSize(0));
    }
}
