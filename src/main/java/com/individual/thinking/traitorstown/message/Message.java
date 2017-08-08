package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.model.Player;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class Message {
    private final Long gameId;
    private final List<Player> recipients;
    private final String content;
}
