package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {

    void addLike(long film_id, long user_id);

    void removeLike(long film_id, long user_id);

    List<Film> getPopularFilms(int count);

}
