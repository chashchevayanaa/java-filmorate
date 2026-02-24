package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User testUser;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("john_doe");
        testUser.setName("John Doe");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }


    @Test
    void shouldAddValidUser() {
        User added = userController.addUser(testUser);
        assertNotNull(added.getId());
        assertEquals(1, added.getId());
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        testUser.setEmail("");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.addUser(testUser));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        testUser.setEmail(null);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.addUser(testUser));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    void shouldThrowWhenEmailHasNoAt() {
        testUser.setEmail("testexample.com");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.addUser(testUser));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLoginIsBlank() {
        testUser.setLogin("");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.addUser(testUser));
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLoginIsNull() {
        testUser.setLogin(null);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.addUser(testUser));
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLoginContainsSpaces() {
        testUser.setLogin("john doe");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.addUser(testUser));
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLoginHasLeadingSpace() {
        testUser.setLogin(" john");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.addUser(testUser));
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        testUser.setBirthday(LocalDate.now().plusDays(1));
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.addUser(testUser));
        assertEquals("Дата рождения не может быть в будущем", ex.getMessage());
    }

    @Test
    void shouldNotThrowWhenBirthdayIsToday() {
        testUser.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> userController.addUser(testUser));
    }

    @Test
    void shouldNotThrowWhenBirthdayIsPast() {
        testUser.setBirthday(LocalDate.now().minusDays(1));
        assertDoesNotThrow(() -> userController.addUser(testUser));
    }

    @Test
    void shouldSetNameToLoginWhenNameIsBlank() {
        testUser.setName("");
        User added = userController.addUser(testUser);
        assertEquals(testUser.getLogin(), added.getName());
    }

    @Test
    void shouldSetNameToLoginWhenNameIsNull() {
        testUser.setName(null);
        User added = userController.addUser(testUser);
        assertEquals(testUser.getLogin(), added.getName());
    }

    @Test
    void shouldUpdateValidUser() {
        User added = userController.addUser(testUser);
        added.setName("New Name");
        User updated = userController.addUser(added);
        assertEquals("New Name", updated.getName());
    }

    @Test
    void shouldSetNameToLoginOnUpdateWhenNameBlank() {
        User added = userController.addUser(testUser);
        added.setName("");
        User updated = userController.updateUser(added);
        assertEquals(added.getLogin(), updated.getName());
    }

    @Test
    void shouldThrowWhenUpdateWithInvalidUser() {
        User added = userController.addUser(testUser);
        added.setEmail("invalid");
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.updateUser(added));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }
}