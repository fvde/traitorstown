package com.individual.thinking.traitorstown.model;

import lombok.Value;

@Value
public class Message {
    private final Long gameId;
    private final Visibility visibility;
    private final String content;

    public boolean visibleToPlayer(Player player){
        // TODO player to player messaging
        return true;
    }
}
