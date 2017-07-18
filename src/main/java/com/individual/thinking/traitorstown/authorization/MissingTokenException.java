package com.individual.thinking.traitorstown.authorization;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class MissingTokenException extends Exception {
    MissingTokenException(String message) {
        super(message);
    }
}
