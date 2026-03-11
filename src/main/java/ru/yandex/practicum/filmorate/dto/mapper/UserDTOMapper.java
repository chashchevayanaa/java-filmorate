package ru.yandex.practicum.filmorate.dto.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class UserDTOMapper {

    public UserDTO toDto(User user) {

        return new UserDTO(user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
    }

    public User fromDto(UserDTO userDTO) {

        return new User(userDTO.getId(), userDTO.getEmail(), userDTO.getLogin(), userDTO.getName(), userDTO.getBirthday());
    }
}
