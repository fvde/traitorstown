package com.individual.thinking.traitorstown.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class PlayerUnauthorizedException extends Exception {
    public PlayerUnauthorizedException(String message) {
        super(message);
    }
}
