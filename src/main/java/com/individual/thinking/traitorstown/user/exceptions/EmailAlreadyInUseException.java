package com.individual.thinking.traitorstown.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailAlreadyInUseException extends Exception {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
