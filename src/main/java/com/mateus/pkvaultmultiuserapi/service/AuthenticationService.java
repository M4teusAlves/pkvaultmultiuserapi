package com.mateus.pkvaultmultiuserapi.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.mateus.pkvaultmultiuserapi.dto.Authentication.AuthenticationLoginRequestDTO;
import com.mateus.pkvaultmultiuserapi.dto.Authentication.AuthenticationLoginResponseDTO;
import com.mateus.pkvaultmultiuserapi.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final TokenService tokenService;

    private final DockerService dockerService;

    
    public AuthenticationLoginResponseDTO login(AuthenticationLoginRequestDTO authenticationLoginRequestDTO) {

        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                authenticationLoginRequestDTO.login(), authenticationLoginRequestDTO.password());

        var authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        var token = tokenService.generateToken((User) authentication.getPrincipal());

        dockerService.startContainer((User) authentication.getPrincipal());

        userService.updateLastHeartbeat((User) authentication.getPrincipal());

        return new AuthenticationLoginResponseDTO(token);
    }
    
}
