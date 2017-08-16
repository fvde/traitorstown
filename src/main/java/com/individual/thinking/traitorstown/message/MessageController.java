package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.game.authorization.AuthorizedPlayer;
import com.individual.thinking.traitorstown.game.exceptions.PlayerUnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @GetMapping(value = "/messages/{gameId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Message>> messages(@PathVariable Long gameId, HttpServletRequest request) throws PlayerUnauthorizedException {
        AuthorizedPlayer authorizedPlayer = new AuthorizedPlayer(request).authorize(gameId, null);
        return messageService.subscribe(gameId, authorizedPlayer.getPlayer().getId());
    }
}
