package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRowMapper userRowMapper;

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("select * from users", userRowMapper);
    }

    @Override
    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.stream().findFirst();
    }

    @Override
    public User add(User user) {
        jdbcTemplate.update(
                "insert into users (name, email, login, birthday) values (?, ?, ?, ?)",
                user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());

        return getByLogin(user.getLogin());
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update(
                "update users set name = ?, email = ?, login = ?, birthday = ? where id = ?",
                user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());

        return user;
    }

    private User getByLogin(String login) {
        String query = "select * from users where login = ?";
        return jdbcTemplate.queryForObject(query, userRowMapper, login);
    }
}
