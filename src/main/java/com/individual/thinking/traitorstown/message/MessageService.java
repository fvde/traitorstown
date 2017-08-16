package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final EmitterProcessor<ServerSentEvent<Message>> emitter;

    MessageService() {
        emitter = EmitterProcessor.create();
    }

    public Flux<ServerSentEvent<Message>> subscribe(Long gameId, Long playerId) {
        return emitter.filter(
                msg -> msg.data().getGameId().equals(gameId)
                && msg.data().getRecipients().contains(playerId));
    }

    public void publishMessage(Game game, String content){
        publishMessage(game.getId(), content, game.getPlayers());
    }

    public void publishMessage(Game game, String content, Player player){
        publishMessage(game.getId(), content, Collections.singletonList(player));
    }

    private void publishMessage(Long game, String content, List<Player> recipients){
        emitter.onNext(ServerSentEvent.<Message>builder()
                        .id(UUID.randomUUID().toString())
                        .event("message")
                        .data(Message.builder()
                                .gameId(game)
                                .recipients(recipients.stream().map(Player::getId).collect(Collectors.toList()))
                                .content(content)
                                .build())
                        .build());
    }
}
