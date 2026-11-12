package com.mateus.pkvaultmultiuserapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mateus.pkvaultmultiuserapi.dto.user.CreateUserDTO;
import com.mateus.pkvaultmultiuserapi.dto.user.UserMeResponseDTO;
import com.mateus.pkvaultmultiuserapi.dto.user.UserResponseDTO;
import com.mateus.pkvaultmultiuserapi.exception.User.UserAlreadyExistsException;
import com.mateus.pkvaultmultiuserapi.model.User;
import com.mateus.pkvaultmultiuserapi.model.Container;
import com.mateus.pkvaultmultiuserapi.repository.ContainerRepository;
import com.mateus.pkvaultmultiuserapi.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final DockerService dockerService;

    private final EmailService emailService;

    private final ContainerRepository containerRepository;

    @Transactional
    public UserMeResponseDTO me(String login) {
        User user = (User) userRepository.findByLogin(login);

        Container container = containerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Container not found for user: " + user.getUsername()));

        String url = "http://localhost:" + container.getPort();

        return new UserMeResponseDTO(user.getLogin(), url);
    }

    @Transactional
    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {

        if (userRepository.existsByLogin(createUserDTO.login())) {
            throw new UserAlreadyExistsException("User with login " + createUserDTO.login() + " already exists.");
        }

        if (userRepository.existsByEmail(createUserDTO.email())) {
            throw new UserAlreadyExistsException("User with email " + createUserDTO.email() + " already exists.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(createUserDTO.password());

        User user = new User(createUserDTO.login(), createUserDTO.email(), encryptedPassword);

        userRepository.save(user);

        try {
            emailService.sendActivationEmail(user.getEmail(), user.getActivationToken());
            System.out.println("Email de ativação enviado para: " + user.getEmail());
        } catch (Exception e) {
            System.out.println("Falha ao enviar email de ativação para: " + user.getEmail());
        }

        var userReponseDTO = new UserResponseDTO(user);

        return userReponseDTO;
    }

    @Transactional
    public UserDetails findUserDetailsByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Transactional
    public List<UserResponseDTO> findAllUsers() {

        List<User> users = userRepository.findAll();

        List<UserResponseDTO> usersResponse = new ArrayList<>();

        for (User user : users) {

            UserResponseDTO userResponse = new UserResponseDTO(user);

            usersResponse.add(userResponse);

        }

        return usersResponse;

    }

    @Transactional
    public UserResponseDTO activeUser(String token) {

        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        if (user.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expired activation token");
        }

        if (user.isActived()) {

            new RuntimeException("User is already active");

        } else {

            user.setActived(!user.isActived());

            userRepository.save(user);

            dockerService.createContainer(user);

            user.setActived(true);
            user.setActivationToken(null);
            user.setActivationTokenExpiry(null);
            userRepository.save(user);

        }

        return new UserResponseDTO(user);
    }

    @Transactional
    public void updateLastHeartbeat(User user) {

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setLastHeartbeat(LocalDateTime.now());
        userRepository.save(user);
    }

}
