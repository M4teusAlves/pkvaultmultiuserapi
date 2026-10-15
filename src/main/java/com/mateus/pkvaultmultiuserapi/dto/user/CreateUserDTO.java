package com.mateus.pkvaultmultiuserapi.dto.user;

public record CreateUserDTO(
    String login,
    String password,
    String email
) {
    
}
