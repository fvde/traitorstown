package com.individual.thinking.traitorstown.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class IncorrectPasswordException extends Exception {
    public IncorrectPasswordException(String message) {
    }
}
