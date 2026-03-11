package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM friendship");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoUsers() {
        List<User> users = userStorage.getAll();
        assertThat(users).isEmpty();
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(1995, 5, 5));

        userStorage.add(user1);
        userStorage.add(user2);

        List<User> users = userStorage.getAll();
        assertThat(users).hasSize(2)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder("login1", "login2");
    }

    @Test
    void getById_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User created = userStorage.add(user);

        Optional<User> found = userStorage.getById(created.getId());
        assertThat(found).isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(created.getId());
                    assertThat(u.getEmail()).isEqualTo("test@mail.ru");
                    assertThat(u.getLogin()).isEqualTo("testLogin");
                });
    }

    @Test
    void getById_ShouldReturnEmptyOptional_WhenNotExists() {
        Optional<User> found = userStorage.getById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void add_ShouldCreateUserAndSetId() {
        User user = new User();
        user.setEmail("new@mail.ru");
        user.setLogin("newLogin");
        user.setName("New User");
        user.setBirthday(LocalDate.of(1985, 10, 10));

        User created = userStorage.add(user);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getLogin()).isEqualTo("newLogin");

        Optional<User> saved = userStorage.getById(created.getId());
        assertThat(saved).isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getEmail()).isEqualTo("new@mail.ru");
                    assertThat(u.getName()).isEqualTo("New User");
                });
    }

    @Test
    void update_ShouldModifyExistingUser() {
        User user = new User();
        user.setEmail("original@mail.ru");
        user.setLogin("origLogin");
        user.setName("Original");
        user.setBirthday(LocalDate.of(1991, 2, 2));
        User created = userStorage.add(user);

        created.setEmail("updated@mail.ru");
        created.setName("Updated Name");
        User updated = userStorage.update(created);

        assertThat(updated.getEmail()).isEqualTo("updated@mail.ru");
        assertThat(updated.getName()).isEqualTo("Updated Name");

        Optional<User> fromDb = userStorage.getById(created.getId());
        assertThat(fromDb).isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getEmail()).isEqualTo("updated@mail.ru");
                    assertThat(u.getName()).isEqualTo("Updated Name");
                });
    }

    @Test
    void update_ShouldThrowException_WhenUserDoesNotExist() {
        User user = new User();
        user.setId(999L);
        user.setEmail("no@mail.ru");
        user.setLogin("noLogin");
        user.setName("No");
        user.setBirthday(LocalDate.now());

    }
}