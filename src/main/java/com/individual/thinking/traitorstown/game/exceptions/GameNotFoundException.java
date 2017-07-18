package com.individual.thinking.traitorstown.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GameNotFoundException extends Throwable {
    public GameNotFoundException(String message) {
        super(message);
    }
}
