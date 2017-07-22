package com.individual.thinking.traitorstown.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PlayerNotInGameException extends Throwable {
    public PlayerNotInGameException(String message) {
        super(message);
    }
}
