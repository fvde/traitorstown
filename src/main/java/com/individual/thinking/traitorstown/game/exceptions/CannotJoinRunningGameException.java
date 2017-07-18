package com.individual.thinking.traitorstown.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CannotJoinRunningGameException extends Exception {
    public CannotJoinRunningGameException(String message) {
        super(message);
    }
}
