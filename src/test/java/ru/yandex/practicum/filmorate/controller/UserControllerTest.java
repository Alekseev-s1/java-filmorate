package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static URI uri;
    private static ObjectMapper objectMapper;

    private static final String EMAIL = "test@test.ru";
    private static final String NAME = "Test name";
    private static final String LOGIN = "Test login";
    private static final LocalDate BIRTHDAY = LocalDate.now().minusYears(27);

    @BeforeAll
    public static void setUp() {
        uri = URI.create("http://localhost:8080/users");
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void shouldThrowExceptionIfEmailIsNull() throws Exception {
        User user = User.builder()
                .name(NAME)
                .login(LOGIN)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Электронная почта не может быть пустой!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldThrowExceptionIfEmailIsBlank() throws Exception {
        User user = User.builder()
                .email("     ")
                .name(NAME)
                .login(LOGIN)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Электронная почта не может быть пустой!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldThrowExceptionIfInvalidEmail() throws Exception {
        User user = User.builder()
                .email("testtest.ru")
                .name(NAME)
                .login(LOGIN)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Электронная почта " + user.getEmail() + " должна содержать символ @!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldThrowExceptionIfLoginIsNull() throws Exception {
        User user = User.builder()
                .email(EMAIL)
                .name(NAME)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Login не может быть пустым!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldThrowExceptionIfLoginIsBlank() throws Exception {
        User user = User.builder()
                .email(EMAIL)
                .name(NAME)
                .login("     ")
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Login не может быть пустым!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldThrowExceptionIfBirthdayFromFuture() throws Exception {
        User user = User.builder()
                .email(EMAIL)
                .name(NAME)
                .login(LOGIN)
                .birthday(LocalDate.now().plusDays(1))
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Дата рождения " + user.getBirthday() + " не может быть больше текущей " + LocalDate.now() + "!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldSetNameIfNameIsNull() throws Exception {
        User user = User.builder()
                .email(EMAIL)
                .login(LOGIN)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(LOGIN));
    }

    @Test
    void shouldSetNameIfNameIsBlank() throws Exception {
        User user = User.builder()
                .email(EMAIL)
                .name("     ")
                .login(LOGIN)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(post(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(LOGIN));
    }

    @Test
    void shouldThrowExceptionIfIdIs0() throws Exception {
        User user = User.builder()
                .email(EMAIL)
                .name(NAME)
                .login(LOGIN)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(put(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemNotFoundException))
                .andExpect(result -> assertEquals("Пользователь с id = 0 не найден!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldThrowExceptionIfUpdatedFilmNotExist() throws Exception {
        User user = User.builder()
                .id(10)
                .email(EMAIL)
                .name(NAME)
                .login(LOGIN)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(put(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemNotFoundException))
                .andExpect(result -> assertEquals("Пользователь с id = 10 не найден!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldThrowExceptionIfNegativeId() throws Exception {
        User user = User.builder()
                .id(-1)
                .email(EMAIL)
                .name(NAME)
                .login(LOGIN)
                .birthday(BIRTHDAY)
                .build();

        this.mockMvc
                .perform(put(uri).content(objectMapper.writeValueAsString(user)).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemNotFoundException))
                .andExpect(result -> assertEquals("Пользователь с id = -1 не найден!",
                        result.getResolvedException().getMessage()));
    }
}
