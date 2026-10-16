package com.mateus.pkvaultmultiuserapi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mateus.pkvaultmultiuserapi.dto.user.UserResponseDTO;
import com.mateus.pkvaultmultiuserapi.model.User;
import com.mateus.pkvaultmultiuserapi.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PutMapping("activate/{token}")
    public ResponseEntity<UserResponseDTO> activeUser(@PathVariable String token) {
        UserResponseDTO userResponseDTO = userService.activeUser(token);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping
    public ResponseEntity<Void> heartbeat() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        userService.updateLastHeartbeat(user);

        return ResponseEntity.ok().build();
    }
    
    
    
}
