package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FriendDbStorage.class, UserDbStorage.class, UserRowMapper.class})
class FriendDbStorageTest {

    @Autowired
    private FriendDbStorage friendStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user1, user2, user3;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM friendship");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        user1 = createUser("user1@mail.ru", "login1", "User1");
        user2 = createUser("user2@mail.ru", "login2", "User2");
        user3 = createUser("user3@mail.ru", "login3", "User3");
    }

    private User createUser(String email, String login, String name) {
        User u = new User();
        u.setEmail(email);
        u.setLogin(login);
        u.setName(name);
        u.setBirthday(LocalDate.of(2000, 1, 1));
        return userStorage.add(u);
    }

    @Test
    void addFriend_ShouldCreateFriendship() {
        friendStorage.addFriend(user1.getId(), user2.getId());

        List<User> friendsOfUser1 = friendStorage.getFriends(user1.getId());
        assertThat(friendsOfUser1).hasSize(1)
                .extracting(User::getId)
                .containsExactly(user2.getId());
    }

    @Test
    void removeFriend_ShouldDeleteFriendship() {
        friendStorage.addFriend(user1.getId(), user2.getId());
        friendStorage.removeFriend(user1.getId(), user2.getId());

        List<User> friendsOfUser1 = friendStorage.getFriends(user1.getId());
        assertThat(friendsOfUser1).isEmpty();
    }

    @Test
    void getFriends_ShouldReturnAllFriendsOfUser() {
        friendStorage.addFriend(user1.getId(), user2.getId());
        friendStorage.addFriend(user1.getId(), user3.getId());

        List<User> friends = friendStorage.getFriends(user1.getId());
        assertThat(friends).hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user2.getId(), user3.getId());
    }

    @Test
    void getCommonFriends_ShouldReturnIntersection() {
        friendStorage.addFriend(user1.getId(), user2.getId());
        friendStorage.addFriend(user3.getId(), user2.getId()); // общий друг user2
        friendStorage.addFriend(user1.getId(), user3.getId()); // не общий

        List<User> common = friendStorage.getCommonFriends(user1.getId(), user3.getId());
        assertThat(common).hasSize(1)
                .extracting(User::getId)
                .containsExactly(user2.getId());
    }
}