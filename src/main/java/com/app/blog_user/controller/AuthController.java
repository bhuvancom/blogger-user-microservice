package com.app.blog_user.controller;


import com.app.blog_user.exception.BlogAPIException;
import com.app.blog_user.model.RegisterDto;
import com.app.blog_user.model.User;
import com.app.blog_user.payload.LoginDto;
import com.app.blog_user.repository.UserRepository;
import com.app.blog_user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // Log in api
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<User> register(@RequestBody RegisterDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @GetMapping("/findBy/{findBy}/{value}")
    public ResponseEntity<User> findUser(@PathVariable String value, @PathVariable String findBy) {
        Optional<User> byUsername = switch (findBy) {
            case "email" -> userRepository.findByEmail(value);
            case "userName" -> userRepository.findByUsername(value);
            case "emailOrUserName" -> {
                Optional<User> find = userRepository.findByUsername(value);
                if (find.isPresent()) yield find;
                else {
                    find = userRepository.findByEmail(value);
                    if (find.isPresent()) yield find;
                    else {
                        throw new BlogAPIException(HttpStatus.BAD_REQUEST, "User is not found for find by : " + findBy + " value: " + value);
                    }
                }
            }
            case "id" -> {
                try {
                    yield userRepository.findById(Long.parseLong(value));
                } catch (Exception ignored) {
                    throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Id is not correct: " + value);
                }
            }
            default ->
                    throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Unexpected findBy value: " + findBy + " eligible findBy options are : email, userName, emailOrUserName, id ");
        };

        return byUsername.map(ResponseEntity::ok).orElseThrow(() -> new BlogAPIException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
