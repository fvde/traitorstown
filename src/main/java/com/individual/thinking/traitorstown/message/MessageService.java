package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ApplicationEventPublisher eventPublisher;

    public void sendMessage(Game game, String content){
        sendMessage(game.getId(), content, game.getPlayers());
    }

    public void sendMessage(Game game, String content, Player player){
        sendMessage(game.getId(), content, Collections.singletonList(player));
    }

    private void sendMessage(Long game, String content, List<Player> recipients){
        eventPublisher.publishEvent(Message.builder()
                        .gameId(game)
                        .recipients(recipients)
                        .content(content)
                .build());
    }
}
