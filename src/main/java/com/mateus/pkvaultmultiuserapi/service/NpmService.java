package com.mateus.pkvaultmultiuserapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NpmService {

    private final RestClient restClient;

    @Value("${npm.api.identity}")
    private String identity;

    @Value("${npm.api.secret}")
    private String secret;

    @Value("${npm.certificate-id}")
    private Integer certificateId;

    @Value("${npm.domain-suffix}")
    private String domainSuffix;

    public NpmService(@Value("${npm.api.base-url}") String baseUrl) {
        this.restClient = RestClient.create(baseUrl);
    }

    private String getAuthToken() {
        Map<String, String> credentials = Map.of(
            "identity", identity,
            "secret", secret
        );

        Map<?, ?> response = restClient.post()
            .uri("/tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .body(credentials)
            .retrieve()
            .body(Map.class);

        return response != null ? (String) response.get("token") : null;
    }

    public Integer createProxyHost(String usernamePrefix, String containerHost, int containerPort) {
        String fullDomain = usernamePrefix.toLowerCase().replaceAll("[^a-z0-9-]", "") + domainSuffix;
        String token = getAuthToken();

        Map<String, Object> body = new HashMap<>();
        body.put("domain_names", List.of(fullDomain));
        body.put("forward_scheme", "http");
        body.put("forward_host", containerHost);
        body.put("forward_port", containerPort);
        body.put("certificate_id", certificateId);
        body.put("ssl_forced", true);
        body.put("http2_support", true);
        body.put("allow_websocket_upgrade", true);
        body.put("block_exploits", true);
        body.put("advanced_config", "");
        body.put("locations", List.of());
        body.put("meta", Map.of("letsencrypt_agree", false, "dns_challenge", false));

        Map<?, ?> res = restClient.post()
            .uri("/nginx/proxy-hosts")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(Map.class);

        return res != null ? (Integer) res.get("id") : null;
    }

    public void deleteProxyHost(Integer proxyHostId) {
        if (proxyHostId == null) return;
        try {
            String token = getAuthToken();
            restClient.delete()
                .uri("/nginx/proxy-hosts/{id}", proxyHostId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete proxy host with ID: " + proxyHostId, e);
        }
    }
}
