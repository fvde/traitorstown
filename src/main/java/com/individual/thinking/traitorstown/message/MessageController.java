package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.game.authorization.AuthorizedPlayer;
import com.individual.thinking.traitorstown.game.exceptions.PlayerUnauthorizedException;
import com.individual.thinking.traitorstown.model.Message;
import com.individual.thinking.traitorstown.model.Player;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class MessageController {

    ConcurrentHashMap<Long, ConcurrentHashMap<Player, SseEmitter>> gameEmitters = new ConcurrentHashMap<>();

    @GetMapping("/messages/{gameId}")
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
        gameEmitters.get(message.getGameId()).forEach((player, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name("Message")
                        .data(message));
            } catch (Exception e) {
                emitter.completeWithError(e);
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
