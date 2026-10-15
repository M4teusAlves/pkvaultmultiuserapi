package com.mateus.pkvaultmultiuserapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mateus.pkvaultmultiuserapi.dto.user.UserResponseDTO;
import com.mateus.pkvaultmultiuserapi.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/teste")
    public String teste() {
        return new String("Testste");
    }

    @GetMapping("/teste2")
    public String teste2() {
        return new String("Testste ADMIN");
    }

    @PutMapping("activate/{token}")
    public ResponseEntity<UserResponseDTO> activeUser(@PathVariable String token) {
        UserResponseDTO userResponseDTO = userService.activeUser(token);
        return ResponseEntity.ok(userResponseDTO);
    }
    
    
    
}
