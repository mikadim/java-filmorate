package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService service;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody @Valid @NotNull UserRequestDto dto) {
        User user = service.addUser(dto);
        log.info("Добавлен новый пользователь: {}", user.toString());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody @Valid @NotNull UserRequestDto dto) {
        User user = service.updateUser(dto);
        log.info("Обновление данных пользователя: {}", user.toString());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(service.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(service.getUser(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        service.addFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getUserFriends(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(service.getUserFriends(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        return new ResponseEntity<>(service.getCommonFriends(id, otherId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        service.deleteFriends(id, friendId);
    }

    @DeleteMapping(value = "/clear-for-test")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUsers() {
        service.delete();
    }
}