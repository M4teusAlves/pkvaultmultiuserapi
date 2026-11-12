package com.mateus.pkvaultmultiuserapi.dto.Authentication;

public record LoginResult(
    String token,
    AuthenticationLoginResponseDTO body
) {
    
}
