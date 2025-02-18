package com.example.finv2.controller;

import com.example.finv2.dto.ResponseDTO;
import com.example.finv2.request.AuthRequest;
import com.example.finv2.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody AuthRequest authRequest) {
        Map<String, String> token = userService.signUp(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {
        Map<String, String> token = userService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO> profile(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(new ResponseDTO<>("Profile success", userService.profile(token), "200"));
    }


//    list all users
    @GetMapping("/users")
    public ResponseEntity<ResponseDTO> listAllUsers(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(new ResponseDTO<>("List all users success", userService.findAllUsers(token), "200"));
    }

}