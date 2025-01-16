package com.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/users")
    public ResponseEntity<User> getUser() {
        return ResponseEntity.ok(new User("John", "john@test.com"));
    }

    record User(String name, String email) {};

}