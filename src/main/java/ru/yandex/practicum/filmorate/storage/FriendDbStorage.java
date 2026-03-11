package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;

import java.util.List;

@Slf4j
@Component
public class FriendDbStorage implements FriendStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRowMapper userRowMapper;

    @Override
    public void addFriend(Long userId, Long friendId) {
        removeFriend(userId, friendId);
        jdbcTemplate.update("insert into friendship (user_id, friend_id) values (?, ?)", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update("delete from friendship where user_id = ? and friend_id = ?", userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String query = """
                select * from users where id in (select friend_id from friendship where user_id = ?)
                """;
        return jdbcTemplate.query(query, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        String query = """
                select * from users
                where id in (
                select friend_id from friendship where user_id = ?
                intersect
                select friend_id from friendship where user_id = ?
                )
                """;
        return jdbcTemplate.query(query, userRowMapper, userId, otherId);
    }
}
