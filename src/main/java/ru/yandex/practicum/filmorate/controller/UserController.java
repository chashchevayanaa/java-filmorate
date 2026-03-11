package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.mapper.UserDTOMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDTOMapper userDTOMapper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return new ResponseEntity<>(userService.getAll()
                .stream()
                .map(userDTOMapper::toDto)
                .toList(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<UserDTO>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getById(id).map(userDTOMapper::toDto), HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<Optional<UserDTO>> add(@RequestBody UserDTO userDTO) {
        User user = userDTOMapper.fromDto(userDTO);
        return new ResponseEntity<>(userService.add(user).map(userDTOMapper::toDto), HttpStatusCode.valueOf(200));
    }

    @PutMapping
    public ResponseEntity<Optional<UserDTO>> update(@RequestBody UserDTO userDTO) {
        User user = userDTOMapper.fromDto(userDTO);
        return new ResponseEntity<>(userService.update(user).map(userDTOMapper::toDto), HttpStatusCode.valueOf(200));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<UserDTO>> getFriends(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getFriends(id)
                .stream()
                .map(userDTOMapper::toDto)
                .toList(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDTO> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId)
                .stream()
                .map(userDTOMapper::toDto)
                .toList();
    }
}
