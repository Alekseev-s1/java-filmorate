package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static URI uri;

    private static final String FILM_NAME = "Test name";
    private static final String FILM_DESC = "Test description";
    private static final LocalDate RELEASE_DATE = LocalDate.now().minusYears(1);
    private static final Duration DURATION = Duration.ofMinutes(70);

    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        uri = URI.create("http://localhost:8080/films");
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void shouldThrowExceptionIfNameIsNull() throws Exception {
        Film film = Film.builder()
                .description(FILM_DESC)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.name", is("Поле name должно быть заполнено")));
    }

    @Test
    void shouldThrowExceptionIfNameIsBlank() throws Exception {
        Film film = Film.builder()
                .name("     ")
                .description(FILM_DESC)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.name", is("Поле name должно быть заполнено")));
    }

    @Test
    void createFilmIfDescription200Chars() throws Exception {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description("Amet in vel dapibus cras non non morbi sapien ornare tortor, interdum orci, quam, " +
                        "arcu nec interdum vestibulum pulvinar accumsan tempus dolor nec interdum morbi nec " +
                        "dictumst. Dui ornare ornare cras no")
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .mpa(MPARating.G)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldThrowExceptionIfDescriptionMore200Chars() throws Exception {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description("Amet in vel dapibus cras non non morbi sapien ornare tortor, interdum orci, quam, " +
                        "arcu nec interdum vestibulum pulvinar accumsan tempus dolor nec interdum morbi nec " +
                        "dictumst. Dui ornare ornare cras noo")
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.description", is("Значение поля description не может превышать 200 символов")));
    }

    @Test
    void createFilmMinReleaseDate() throws Exception {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESC)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(DURATION)
                .mpa(MPARating.G)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldThrowExceptionIfReleaseDateLessThanMin() throws Exception {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESC)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(DURATION)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string("Дата релиза не может быть раньше 28 декабря 1895"));
    }

    @Test
    void shouldThrowExceptionIfDurationIsNegative() throws Exception {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESC)
                .releaseDate(RELEASE_DATE)
                .duration(Duration.ofMinutes(-1))
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string("Значение поля duration должно быть положительным числом"));
    }

    @Test
    void shouldThrowExceptionIfDurationIs0() throws Exception {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESC)
                .releaseDate(RELEASE_DATE)
                .duration(Duration.ofMinutes(0))
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string("Значение поля duration должно быть положительным числом"));
    }

    @Test
    void shouldThrowExceptionIfIdIs0() throws Exception {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESC)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();

        this.mockMvc
                .perform(put(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string("Фильм с id = 0 не найден!"));
    }

    @Test
    void shouldThrowExceptionIfUpdatedFilmNotExist() throws Exception {
        Film film = Film.builder()
                .id(10)
                .name(FILM_NAME)
                .description(FILM_DESC)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();

        System.out.println(objectMapper.writeValueAsString(film));

        this.mockMvc
                .perform(put(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string("Фильм с id = 10 не найден!"));
    }

    @Test
    void shouldThrowExceptionIfNegativeId() throws Exception {
        Film film = Film.builder()
                .id(-1)
                .name(FILM_NAME)
                .description(FILM_DESC)
                .releaseDate(RELEASE_DATE)
                .duration(DURATION)
                .build();

        this.mockMvc
                .perform(put(uri).content(objectMapper.writeValueAsString(film)).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string("Фильм с id = -1 не найден!"));
    }
}
