package com.individual.thinking.traitorstown.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class GameFullException extends Exception {
    public GameFullException(String message) {
        super(message);
    }
}
