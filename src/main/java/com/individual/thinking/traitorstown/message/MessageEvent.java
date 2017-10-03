package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.model.Message;
import com.individual.thinking.traitorstown.model.Player;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Builder
@Value
public class MessageEvent {
    final Long game;
    final Message payload;
    final List<Long> recipients;
    final Optional<Player> fromPlayer;
    final Optional<Player> toPlayer;

}
