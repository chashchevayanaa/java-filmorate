package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAll() {
        log.debug("Запрос списка всех пользователей");
        return userStorage.getAll();
    }

    public User getById(Long id) {
        log.debug("Запрос пользователя по id: {}", id);
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public User add(User user) {
        log.debug("Добавление нового пользователя: {}", user);
        validate(user);
        handleEmptyName(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        log.debug("Обновление пользователя: {}", user);
        validate(user);
        getById(user.getId()); // проверка существования
        handleEmptyName(user);
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("Пользователь {} добавляет в друзья {}", userId, friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Пользователь {} удаляет из друзей {}", userId, friendId);
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи {} и {} больше не друзья, у них новые друзья", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        log.debug("Запрос друзей пользователя {}", userId);
        User user = getById(userId);
        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(getById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Запрос общих друзей пользователей {} и {}", userId, otherId);
        User user = getById(userId);
        User other = getById(otherId);
        Set<Long> commonIds = user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .collect(Collectors.toSet());
        return commonIds.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    private void validate(User user) {
        log.debug("Валидация пользователя: {}", user);
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void handleEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}