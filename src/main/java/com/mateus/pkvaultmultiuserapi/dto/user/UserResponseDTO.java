package com.mateus.pkvaultmultiuserapi.dto.user;

import java.time.LocalDateTime;

import com.mateus.pkvaultmultiuserapi.model.User;

public record UserResponseDTO(
    String login,
    String email,
    boolean isActived,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

   public UserResponseDTO(User user){
        this(user.getLogin(), user.getEmail(), user.isActived(), user.getCreatedAt(), user.getUpdatedAt());
   }

    
    
}
