package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("POST /users – добавление пользователя: {}", user);
        validateUser(user);
        long newId = getNextId();
        user.setId(newId);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, установлен логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        users.put(newId, user);
        log.info("Пользователь добавлен: id={}", user.getId());
        return user;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("GET /users – запрос списка всех пользователей, всего: {}", users.size());
        return users.values();
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("PUT /users – обновление пользователя: {}", user);
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id={} не найден", user.getId());
            throw new ValidationException("Фильм с id " + user.getId() + " не найден");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя пустое, установлен логин: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлён: id={}", user.getId());
        return user;
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Валидация не пройдена: некорректный email");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Валидация не пройдена: некорректный логин");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Валидация не пройдена: дата рождения в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
