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
import ru.yandex.practicum.filmorate.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private List<User> users = new ArrayList<>();
    private final ConversionService conversionService;
    private final UserMapper userMapper;
    private IdGenerator idGenerator;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(ConversionService conversionService, UserMapper userMapper, IdGenerator idGenerator) {
        this.conversionService = conversionService;
        this.userMapper = userMapper;
        this.idGenerator = idGenerator;
    }

    @PostMapping()
    public ResponseEntity<User> addUser(@RequestBody @Valid @NotNull UserRequestDto dto) {
        dto.setId(idGenerator.getId());
        User user = conversionService.convert(dto, User.class);
        users.add(user);
        log.info("Добавлен новый пользователь: {}", user.toString());
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody @Valid @NotNull UserRequestDto dto) {
        // User user = userMapper.mapToUser(dto);
        User user = conversionService.convert(dto, User.class);

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                log.info("Обновление данных пользователя: {}", user.toString());
                return new ResponseEntity<User>(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
    }

    @GetMapping (produces = APPLICATION_JSON_VALUE)
    public List<User> getAllUsers() {
        return users;
    }

    @DeleteMapping (value = "/clearfortest")
    public void deleteUsers(){
        users.clear();
        idGenerator=new IdGenerator();
    }

}