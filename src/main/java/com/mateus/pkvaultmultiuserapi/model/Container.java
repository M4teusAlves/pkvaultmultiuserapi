package com.mateus.pkvaultmultiuserapi.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mateus.pkvaultmultiuserapi.model.enums.ContainerStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "containers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String containerId;
    private String name;
    private Integer port;
    private Integer proxyHostId;
    @Enumerated(EnumType.STRING)
    private ContainerStatus status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
