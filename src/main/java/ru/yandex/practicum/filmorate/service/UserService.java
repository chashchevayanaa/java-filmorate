package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public List<User> getAll() {
        log.debug("Запрос списка всех пользователей");
        return userStorage.getAll();
    }

    public Optional<User> getById(Long id) {
        log.debug("Запрос пользователя по id: {}", id);
        return Optional.of(userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")));
    }

    public Optional<User> add(User user) {
        log.debug("Добавление нового пользователя: {}", user);
        validate(user);
        handleEmptyName(user);
        return Optional.ofNullable(userStorage.add(user));
    }

    public Optional<User> update(User user) {
        log.debug("Обновление пользователя: {}", user);
        Optional<User> user1 = getById(user.getId());
        validate(user);
        handleEmptyName(user);
        return Optional.ofNullable(userStorage.update(user));
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("Пользователь {} добавляет в друзья {}", userId, friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
        getById(userId);
        getById(friendId);
        friendStorage.addFriend(userId, friendId);
        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Пользователь {} удаляет из друзей {}", userId, friendId);
        getById(userId);
        getById(friendId);
        friendStorage.removeFriend(userId, friendId);
        log.info("Пользователи {} и {} больше не друзья, у них новые друзья", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        log.debug("Запрос друзей пользователя {}", userId);
        getById(userId);
        return friendStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Запрос общих друзей пользователей {} и {}", userId, otherId);
        getById(userId);
        getById(otherId);
        return friendStorage.getCommonFriends(userId, otherId);
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