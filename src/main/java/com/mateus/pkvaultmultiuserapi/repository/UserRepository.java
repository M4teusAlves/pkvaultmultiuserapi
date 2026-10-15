package com.mateus.pkvaultmultiuserapi.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.mateus.pkvaultmultiuserapi.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    public UserDetails findByLogin(String login);

    public boolean existsByEmail(String email);

    public boolean existsByLogin(String login);

    public Optional<User> findByActivationToken(String token);
    
}
