package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.game.authorization.AuthorizedPlayer;
import com.individual.thinking.traitorstown.game.exceptions.PlayerUnauthorizedException;
import com.individual.thinking.traitorstown.game.representation.MessageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @GetMapping(value = "/messages/{gameId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<MessageRepresentation>> messages(@PathVariable Long gameId, HttpServletRequest request) throws PlayerUnauthorizedException {
        AuthorizedPlayer authorizedPlayer = new AuthorizedPlayer(request).authorize(gameId, null);
        return messageService.subscribe(gameId, authorizedPlayer.getPlayer().getId());
    }

    @PostMapping(value = "/messages/{gameId}")
    void sendMessage(@PathVariable Long gameId, @RequestBody MessageVo messageVo, HttpServletRequest request) throws PlayerUnauthorizedException {
        AuthorizedPlayer authorizedPlayer = new AuthorizedPlayer(request).authorize(gameId, null);
        messageService.publishMessage(gameId, messageVo.getContent(), messageVo.getRecipients(), Optional.of(authorizedPlayer.getPlayer()));
    }
}
