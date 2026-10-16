package com.mateus.pkvaultmultiuserapi.repository;

import java.time.LocalDateTime;
import java.util.List;
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

    @Modifying
    @Transactional
    @Query("UPDATE Container c SET c.status = :status WHERE c.containerId = :containerId")
    void updateStatus(@Param("containerId") String containerId, @Param("status") ContainerStatus stopped);

    @Query("SELECT c FROM Container c WHERE c.status = :status AND c.user.lastHeartbeat < :threshold")
    List<Container> findByStatusAndUserLastHeartbeatBefore(
        @Param("status") ContainerStatus status,
        @Param("threshold") LocalDateTime threshold
    );

    void deleteByContainerId(String containerId);

    Optional<Container> findByUser(User user);
    
}
