package com.app.blog_user.service;


import com.app.blog_user.model.RegisterDto;
import com.app.blog_user.model.User;
import com.app.blog_user.model.UserExist;
import com.app.blog_user.payload.LoginDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    String login(LoginDto loginDto);

    User register(RegisterDto user);

    UserExist existsByUsername(String username);

    UserExist existsByEmail(String email);
}
