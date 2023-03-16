package com.wf.spring_jwt_oauth.controller;

import com.wf.spring_jwt_oauth.DTO.RequestLoginDto;
import com.wf.spring_jwt_oauth.entities.User;
import com.wf.spring_jwt_oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthentificationController {
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register( @RequestBody(required = false) User user) {
       return ResponseEntity.ok(this.userService.register(user));
    }


    @PostMapping(value = "/login")
    public ResponseEntity<?> login(String email,
                                   String password,  String refreshToken) {

        return ResponseEntity.ok(this.userService.login(new RequestLoginDto(email, password), refreshToken));
    }
}
