package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Player;
import lombok.Data;

@Data
public class PlayerRepresentation {
    private final Long id;
    private final Boolean ready;

    public static PlayerRepresentation fromPlayer(Player player){
        return new PlayerRepresentation(player.getId(),
                player.getReady());
    }
}
