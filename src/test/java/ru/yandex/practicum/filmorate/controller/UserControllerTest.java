package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAll_ShouldReturn200AndList() throws Exception {
        User user = createTestUser(1L);
        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("test@mail.ru"));
    }

    @Test
    void getById_ShouldReturn200AndUser_WhenExists() throws Exception {
        User user = createTestUser(1L);
        when(userService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_ShouldReturn404_WhenNotFound() throws Exception {
        when(userService.getById(99L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found"));
    }

    @Test
    void add_ShouldReturn200AndUser_WhenValid() throws Exception {
        User user = createTestUser(null);
        User createdUser = createTestUser(1L);
        when(userService.add(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void add_ShouldReturn400_WhenValidationException() throws Exception {
        User user = createTestUser(null);
        when(userService.add(any(User.class))).thenThrow(new ValidationException("Invalid"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid"));
    }

    @Test
    void update_ShouldReturn200AndUser_WhenValid() throws Exception {
        User user = createTestUser(1L);
        when(userService.update(any(User.class))).thenReturn(user);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturn400_WhenValidationException() throws Exception {
        User user = createTestUser(1L);
        when(userService.update(any(User.class))).thenThrow(new ValidationException("Invalid"));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFriend_ShouldReturn200() throws Exception {
        doNothing().when(userService).addFriend(1L, 2L);
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    void removeFriend_ShouldReturn200() throws Exception {
        doNothing().when(userService).removeFriend(1L, 2L);
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    void getFriends_ShouldReturn200AndList() throws Exception {
        User friend = createTestUser(2L);
        when(userService.getFriends(1L)).thenReturn(List.of(friend));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void getCommonFriends_ShouldReturn200AndList() throws Exception {
        User common = createTestUser(3L);
        when(userService.getCommonFriends(1L, 2L)).thenReturn(List.of(common));

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    private User createTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}