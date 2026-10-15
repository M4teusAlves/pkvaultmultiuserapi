package com.mateus.pkvaultmultiuserapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mateus.pkvaultmultiuserapi.dto.Authentication.AuthenticationLoginRequestDTO;
import com.mateus.pkvaultmultiuserapi.dto.Authentication.AuthenticationLoginResponseDTO;
import com.mateus.pkvaultmultiuserapi.dto.user.CreateUserDTO;
import com.mateus.pkvaultmultiuserapi.dto.user.UserResponseDTO;
import com.mateus.pkvaultmultiuserapi.model.User;
import com.mateus.pkvaultmultiuserapi.service.AuthenticationService;
import com.mateus.pkvaultmultiuserapi.service.TokenService;
import com.mateus.pkvaultmultiuserapi.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final AuthenticationService authenticationService;

    private final TokenService tokenService;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationLoginResponseDTO> login(@RequestBody @Valid AuthenticationLoginRequestDTO authenticationLoginDTO) {

        AuthenticationLoginResponseDTO response = authenticationService.login(authenticationLoginDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid CreateUserDTO createUserDTO) {
        return ResponseEntity.ok(userService.createUser(createUserDTO));
    }
    
    
    
}
