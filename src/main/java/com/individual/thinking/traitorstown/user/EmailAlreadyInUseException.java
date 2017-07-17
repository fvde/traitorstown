package com.individual.thinking.traitorstown.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class EmailAlreadyInUseException extends Exception {
    EmailAlreadyInUseException(String message) {
        super(message);
    }
}
