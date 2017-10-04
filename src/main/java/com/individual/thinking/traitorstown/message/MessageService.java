package com.individual.thinking.traitorstown.message;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {

    private final DirectProcessor<MessageEvent> messages;
    private Map<Long, List<FluxSink<?>>> activeGames = new HashMap<>();

    private final TraitorsTownConfiguration configuration;
    public static EventBus EventBus = new EventBus();

    @Autowired
    MessageService(TraitorsTownConfiguration configuration) {
        this.configuration = configuration;
        this.messages = DirectProcessor.create();
        this.EventBus.register(this);
    }

    public Flux<MessageEvent> subscribe(Long gameId, Long playerId) {
        return Flux.create(sink -> {
            messages.subscribe(message -> {
                if (message.getRecipients().contains(playerId)
                        && message.getGameId().equals(gameId)) {
                    sink.next(message);
                }
            });

            addSession(gameId, sink);
        });
    }

    private void addSession(Long gameId, FluxSink<MessageEvent> sink) {
        if(activeGames.containsKey(gameId)){
            activeGames.get(gameId).add(sink);
        } else {
            LinkedList<FluxSink<?>> connections = new LinkedList<>();
            connections.add(sink);
            activeGames.put(gameId, connections);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void publishMessage(MessageEvent message) {
        log.info("Publishing message {}", message);
        if (!configuration.getMessagingEnabled()) return;
        messages.onNext(message);
    }

    public void sendMessageToGame(Game game, String content){
        publishMessage(
                game.getId(),
                content,
                game.getPlayers().stream().map(Player::getId).collect(Collectors.toList()),
                Optional.empty());
    }

    public void publishMessage(Long game, String content, List<Long> recipients, Optional<Player> fromPlayer){
        publishMessage(MessageEvent.builder()
                .content(content)
                .gameId(game)
                .recipients(recipients)
                .from(fromPlayer.isPresent() ? fromPlayer.get().getId() : -1L)
                .build());
    }
}
