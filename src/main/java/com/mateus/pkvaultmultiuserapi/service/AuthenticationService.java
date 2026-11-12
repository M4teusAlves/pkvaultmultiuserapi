package com.mateus.pkvaultmultiuserapi.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.mateus.pkvaultmultiuserapi.dto.Authentication.AuthenticationLoginRequestDTO;
import com.mateus.pkvaultmultiuserapi.dto.Authentication.AuthenticationLoginResponseDTO;
import com.mateus.pkvaultmultiuserapi.dto.Authentication.LoginResult;
import com.mateus.pkvaultmultiuserapi.model.Container;
import com.mateus.pkvaultmultiuserapi.model.User;
import com.mateus.pkvaultmultiuserapi.repository.ContainerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final TokenService tokenService;

    private final DockerService dockerService;

    private final ContainerRepository containerRepository;

    
    public LoginResult login(AuthenticationLoginRequestDTO authenticationLoginRequestDTO) {

        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                authenticationLoginRequestDTO.login(), authenticationLoginRequestDTO.password());

        var authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        var token = tokenService.generateToken((User) authentication.getPrincipal());

        var body = new AuthenticationLoginResponseDTO("Login successful");

        dockerService.startContainer((User) authentication.getPrincipal());

        userService.updateLastHeartbeat((User) authentication.getPrincipal());

        return new LoginResult(token, body);
    }

    public void logout(User user) {
        
        Container container = containerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Container not found for user: " + user.getUsername()));

        dockerService.stopContainer(container.getContainerId().toString());
    }
    
}
