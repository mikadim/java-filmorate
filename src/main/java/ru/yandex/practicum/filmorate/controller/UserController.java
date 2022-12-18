package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.dto.UserRequestDto;
import ru.yandex.practicum.filmorate.controller.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.exception.FilmStrorageError;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final ConversionService conversionService;
    private final UserMapper userMapper;
    private final UserService service;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(ConversionService conversionService, UserMapper userMapper, UserService service) {
        this.conversionService = conversionService;
        this.userMapper = userMapper;
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
        User user = service.getUser(id);
        List<User> friends = user.getFriends().stream()
                .map(i -> service.getUser(i))
                .collect(Collectors.toList());
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        Set<Integer> otherFiends = service.getUser(otherId).getFriends();
        User user = service.getUser(id);
        List<User> friends = user.getFriends().stream()
                .filter(i -> otherFiends.contains(i))
                .map(i -> service.getUser(i))
                .collect(Collectors.toList());
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        service.deleteFriends(id, friendId);
    }

    @DeleteMapping(value = "/clear-for-test")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUsers() {
        List<Integer> collect = service.getUsers().stream().map(user -> user.getId()).collect(Collectors.toList());
        for (Integer id : collect) {
            try {
                service.deleteUser(id);
            } catch (FilmStrorageError e) {

            }
        }
    }
}