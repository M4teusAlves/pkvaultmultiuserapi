package com.mateus.pkvaultmultiuserapi.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.mateus.pkvaultmultiuserapi.model.Container;
import com.mateus.pkvaultmultiuserapi.model.User;
import com.mateus.pkvaultmultiuserapi.model.enums.ContainerStatus;
import com.mateus.pkvaultmultiuserapi.repository.ContainerRepository;

import jakarta.transaction.Transactional;

@Service
public class DockerService {

    private final DockerClient dockerClient;

    private final ContainerRepository containerRepository;

    @Value("${docker.image.pkvault}")
    private String pkvaultImage;

    public DockerService(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();

        DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();

        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    @Transactional 
    public String createContainer(User user) {
        
        String containerName = "pkvault_" + user.getLogin();
        
        try {
            List<com.github.dockerjava.api.model.Container> existing =
                dockerClient.listContainersCmd()
                    .withNameFilter(Collections.singleton("/" + containerName))
                    .withShowAll(true)
                    .exec();

            if (!existing.isEmpty()) {
                String existingId = existing.get(0).getId();
                try { dockerClient.stopContainerCmd(existingId).exec(); } catch (Exception ignored) {}
                dockerClient.removeContainerCmd(existingId).exec();
            }
        } catch (Exception ignored) {}

        Integer maxPort = containerRepository.findMaxPort() + 1;

        CreateContainerResponse container = dockerClient.createContainerCmd(pkvaultImage)
                .withName(containerName)
                .withHostConfig(
                    HostConfig.newHostConfig()
                        .withPortBindings(PortBinding.parse(maxPort + ":3000"))
                        .withRestartPolicy(RestartPolicy.alwaysRestart())
                )
                .exec();

        Container containerEntity = new Container();
        containerEntity.setContainerId(container.getId());
        containerEntity.setName(containerName);
        containerEntity.setStatus(ContainerStatus.STOPPED);
        containerEntity.setPort(maxPort);
        containerEntity.setProxyHostId(1);
        containerEntity.setUser(user);
        containerRepository.save(containerEntity);


        return container.getId();
    }

    @Transactional
    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        containerRepository.updateStatus(containerId, ContainerStatus.STOPPED);
    }

    @Transactional
    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId)
                .withForce(true)
                .exec();
        containerRepository.deleteByContainerId(containerId);
    }

    @Transactional
    public void startContainer(User user) {
        Container container = containerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Container not found for user: " + user.getUsername()));
        dockerClient.startContainerCmd(container.getContainerId()).exec();
        containerRepository.updateStatus(container.getContainerId(), ContainerStatus.RUNNING);
    }
}
