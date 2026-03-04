package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.debug("Пользователь с id {} не найден в хранилище", id);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public User add(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен в хранилище: id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id {} не найден при обновлении", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлён в хранилище: id={}", user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
        log.info("Пользователь удалён из хранилища: id={}", id);
    }
}