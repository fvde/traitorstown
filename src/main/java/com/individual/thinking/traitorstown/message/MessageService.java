package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public void sendMessageToGame(Game game, String content){
        publishMessage(
                game.getId(),
                content,
                game.getPlayers().stream().map(Player::getId).collect(Collectors.toList()),
                Optional.empty());
    }

    public void sendMessageToGameFromPlayer(Game game, String content, Player fromPlayer){
        publishMessage(
                game.getId(),
                content,
                game.getPlayers().stream().map(Player::getId).collect(Collectors.toList()),
                Optional.of(fromPlayer));
    }

    public void sendMessageToPlayer(Game game, String content, Player toPlayer){
        publishMessage(
                game.getId(),
                content,
                Collections.singletonList(toPlayer.getId()),
                Optional.empty());
    }

    public void sendMessageToPlayerFromPlayer(Game game, String content, Player fromPlayer, Player toPlayer){
        publishMessage(
                game.getId(),
                content,
                Collections.singletonList(toPlayer.getId()),
                Optional.of(fromPlayer));
    }

    public void publishMessage(Long game, String content, List<Long> recipients, Optional<Player> fromPlayer){
        emitter.onNext(ServerSentEvent.<Message>builder()
                        .id(UUID.randomUUID().toString())
                        .event("message")
                        .data(Message.builder()
                                .gameId(game)
                                .recipients(recipients)
                                .from(fromPlayer.isPresent() ? fromPlayer.get().getId() : -1L)
                                .content(content)
                                .build())
                        .build());
    }
}
