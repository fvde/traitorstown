package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.game.authorization.AuthorizedPlayer;
import com.individual.thinking.traitorstown.game.exceptions.PlayerUnauthorizedException;
import com.individual.thinking.traitorstown.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class MessageController {

    ConcurrentHashMap<Long, ConcurrentHashMap<Player, SseEmitter>> gameEmitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/messages/{gameId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter getMessages(@PathVariable Long gameId, HttpServletRequest request) throws PlayerUnauthorizedException {
        AuthorizedPlayer authorizedPlayer = new AuthorizedPlayer(request).authorize(gameId, null);
        if (!gameEmitters.containsKey(gameId)){
            gameEmitters.put(gameId, new ConcurrentHashMap<>());
        }

        return gameEmitters.get(gameId).put(
                authorizedPlayer.getPlayer(),
                buildEmitter(gameId, authorizedPlayer.getPlayer()));
    }

    @EventListener
    public void onMessage(Message message){
        log.info("Publishing message {}", message);
        if (!gameEmitters.containsKey(message.getGameId())){
            log.info("Publishing message {}, but nobody seems to be listening :(", message);
            return;
        }
        gameEmitters.get(message.getGameId()).forEach((player, emitter) -> {
            if (message.getRecipients().contains(player)){
                try {
                    emitter.send(SseEmitter.event()
                            .id(UUID.randomUUID().toString())
                            .name("Message")
                            .data(message.getContent()),
                            MediaType.TEXT_EVENT_STREAM);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }
        });
    }

    private SseEmitter buildEmitter(Long game, Player player){
        SseEmitter emitter = new SseEmitter();
        emitter.onTimeout(()-> emitter.complete());
        emitter.onCompletion(() -> gameEmitters.get(game).remove(player));
        return emitter;
    }
}
