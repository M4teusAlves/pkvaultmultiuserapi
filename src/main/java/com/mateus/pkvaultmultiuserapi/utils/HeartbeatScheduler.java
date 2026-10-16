package com.mateus.pkvaultmultiuserapi.utils;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mateus.pkvaultmultiuserapi.model.Container;
import com.mateus.pkvaultmultiuserapi.model.enums.ContainerStatus;
import com.mateus.pkvaultmultiuserapi.repository.ContainerRepository;
import com.mateus.pkvaultmultiuserapi.service.DockerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HeartbeatScheduler {

    private final DockerService dockerService;

    private final ContainerRepository containerRepository;

    @Scheduled(fixedDelay = 30000)
    public void checkInactiveUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(60);

        List<Container> runningContainers = containerRepository
                .findByStatusAndUserLastHeartbeatBefore(ContainerStatus.RUNNING, threshold);

        for (Container container : runningContainers) {
            try {
                dockerService.stopContainer(container.getContainerId());
                container.setStatus(ContainerStatus.STOPPED);
                containerRepository.save(container);
            } catch (Exception e) {
                new RuntimeException("Error stopping container " + container.getContainerId() + ": " + e.getMessage());
            }
        }
    }
}
