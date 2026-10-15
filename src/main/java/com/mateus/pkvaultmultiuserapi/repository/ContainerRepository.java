package com.mateus.pkvaultmultiuserapi.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mateus.pkvaultmultiuserapi.model.Container;
import com.mateus.pkvaultmultiuserapi.model.User;
import com.mateus.pkvaultmultiuserapi.model.enums.ContainerStatus;

import jakarta.transaction.Transactional;

public interface ContainerRepository extends JpaRepository<Container, UUID>{
    @Query("SELECT COALESCE(MAX(c.port), 7000) FROM Container c")
    Integer findMaxPort();

    @Modifying                          // ← essa anotação estava faltando
    @Transactional                      // ← necessário para operações de escrita
    @Query("UPDATE Container c SET c.status = :status WHERE c.containerId = :containerId")
    void updateStatus(@Param("containerId") String containerId, @Param("status") ContainerStatus stopped);    

    void deleteByContainerId(String containerId);

    Optional<Container> findByUser(User user);
    
}
