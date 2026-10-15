package com.mateus.pkvaultmultiuserapi.exception.User;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
