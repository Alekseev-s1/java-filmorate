package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@Builder(toBuilder = true)
public class User {
    private long id;

    @NotBlank(message = "Электронная почта (email) обязательна для заполнения")
    @Email(message = "Неверный формат электронной почты (email)")
    private String email;

    @NotBlank(message = "Поле login обязательно для заполнения")
    private String login;
    private String name;

    @PastOrPresent(message = "Дата рождения birthday не может быть больше текущей")
    private LocalDate birthday;
    private List<Long> friendsId;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("login", login);
        map.put("name", name);
        map.put("birthday", birthday);

        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
