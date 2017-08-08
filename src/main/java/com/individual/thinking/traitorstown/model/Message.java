package com.individual.thinking.traitorstown.model;

import lombok.Value;

@Value
public class Message {
    private final String content;
    private final Visibility visibility;

    public boolean visibleToPlayer(Player player){
        // TODO player to player messaging
        return true;
    }
}
