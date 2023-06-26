package com.app.blog_user.service.impl;


import com.app.blog_user.exception.BlogAPIException;
import com.app.blog_user.model.RegisterDto;
import com.app.blog_user.model.Role;
import com.app.blog_user.model.User;
import com.app.blog_user.model.UserExist;
import com.app.blog_user.payload.LoginDto;
import com.app.blog_user.repository.UserRepository;
import com.app.blog_user.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;


    @Override
    public String login(LoginDto loginDto) {
        // TODO add logic to login
        Optional<User> user = userRepository.findByEmail(loginDto.getEmailOrUsername());
        if (user.isPresent()) {
            if (checkIfPasswordSame(user.get().getPassword(), loginDto.getPassword())) {
                return "User logged in successfully.";
            }
            throw new BlogAPIException(HttpStatus.NOT_FOUND, "Please check your username/email/password");
        }
        user = userRepository.findByUsername(loginDto.getEmailOrUsername());
        user.map(u -> {
            if (checkIfPasswordSame(u.getPassword(), loginDto.getPassword())) {
                return "Login success";
            }
            throw new BlogAPIException(HttpStatus.NOT_FOUND, "Please check your username/email/password");
        }).orElseThrow(() -> new BlogAPIException(HttpStatus.NOT_FOUND, "Please check your username/email/password"));
        throw new BlogAPIException(HttpStatus.NOT_FOUND, "Please check your username/email/password");
    }

    private boolean checkIfPasswordSame(String og, String incoming) {
        return passwordEncoder.matches(og, incoming);
    }

    private void throwError() {
        throw new BlogAPIException(HttpStatus.NOT_FOUND, "Please check your username/email/password");
    }

    @Override
    public User register(RegisterDto user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username already exists!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email already exists!");
        }

        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setName(user.getName());
        user1.setUsername(user.getUsername());
        user1.setPassword(user.getPassword());
        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setId(user.getRoleId());
        roles.add(userRole);
        user1.setRoles(roles);

        return userRepository.save(user1);
    }

    @Override
    public UserExist existsByUsername(String username) {
        return new UserExist(userRepository.existsByUsername(username));
    }

    @Override
    public UserExist existsByEmail(String email) {
        return new UserExist(userRepository.existsByEmail(email));
    }
}
