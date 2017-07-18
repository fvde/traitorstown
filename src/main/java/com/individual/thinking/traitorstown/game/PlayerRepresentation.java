package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Player;
import lombok.Data;

@Data
class PlayerRepresentation {
    private final Long id;
    private final Boolean ready;

    protected static PlayerRepresentation fromPlayer(Player player){
        return new PlayerRepresentation(player.getId(),
                player.getReady());
    }
}
