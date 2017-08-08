package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
public class TraitorsTownEmitter extends SseEmitter {
    private final Player receivingPlayer;
}
