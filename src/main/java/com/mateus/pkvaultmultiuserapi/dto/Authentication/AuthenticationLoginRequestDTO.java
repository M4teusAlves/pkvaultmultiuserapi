package com.mateus.pkvaultmultiuserapi.dto.Authentication;

public record AuthenticationLoginRequestDTO(
    String login,
    String password
) {

}