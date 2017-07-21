package com.individual.thinking.traitorstown.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RuleSetViolationException extends Exception {
    public RuleSetViolationException(String message) {
        super(message);
    }
}
