package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.ResourceType;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PlayerRepresentation {
    private final Long id;
    private final Boolean ready;
    private final List<ResourceRepresentation> resources;
    private final List<EffectRepresentation> activeEffects;

    public static PlayerRepresentation fromPlayer(Player player, Player asPlayer){
        return new PlayerRepresentation(
                player.getId(),
                player.isReady(),
                player.getId().equals(asPlayer.getId())
                        ? getResourceRepresentation(player)
                        : Collections.emptyList(),
                player.getActiveEffects().stream()
                        .filter(effect -> effect.isVisibleFor(asPlayer))
                        .map(EffectRepresentation::fromActiveEffect).collect(Collectors.toList()));
    }

    private static List<ResourceRepresentation> getResourceRepresentation(Player player) {
        if (player.isTraitor()){
            return Arrays.stream(ResourceType.values()).map(type -> new ResourceRepresentation(type.ordinal(), player.getResource(type))).collect(Collectors.toList());
        } else {
            return Arrays.stream(ResourceType.values())
                    .filter(type -> type.equals(ResourceType.STOLEN_GOLD))
                    .map(type -> {
                        if (type == ResourceType.GOLD){
                            return new ResourceRepresentation(type.ordinal(), player.getResource(type) + player.getResource(ResourceType.STOLEN_GOLD));
                        } else {
                            return new ResourceRepresentation(type.ordinal(), player.getResource(type));
                        }
                    })
                    .collect(Collectors.toList());
        }
    }
}
