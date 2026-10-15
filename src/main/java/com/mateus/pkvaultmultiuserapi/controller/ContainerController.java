package com.mateus.pkvaultmultiuserapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mateus.pkvaultmultiuserapi.model.User;
import com.mateus.pkvaultmultiuserapi.service.DockerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/container")
@RequiredArgsConstructor
public class ContainerController {

    private final DockerService dockerService;

    @PostMapping
    public ResponseEntity<String> create() {

        User user = (User) SecurityContextHolder.getContext()
                                           .getAuthentication()
                                           .getPrincipal();

        String containerId = dockerService.createContainer(user);
        return ResponseEntity.ok("Container criado: " + containerId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable String id) {
        dockerService.stopContainer(id);
        dockerService.removeContainer(id);
        return ResponseEntity.noContent().build();
    }
}
