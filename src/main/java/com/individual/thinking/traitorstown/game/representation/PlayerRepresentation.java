package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Resource;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class PlayerRepresentation {
    private final Long id;
    private final Boolean ready;
    private final List<ResourceRepresentation> resources;

    public static PlayerRepresentation fromPlayer(Player player){
        return new PlayerRepresentation(
                player.getId(),
                player.getReady(),
                Arrays.asList(
                        new ResourceRepresentation(Resource.GOLD, player.getResource(Resource.GOLD)),
                        new ResourceRepresentation(Resource.REPUTATION, player.getResource(Resource.REPUTATION))));
    }
}
