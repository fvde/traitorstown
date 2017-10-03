package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.effects.ResourceEffect;
import lombok.Data;

@Data
public class ResourceRepresentation {
    private final int type;
    private final Integer amount;

    public static ResourceRepresentation fromEffect(ResourceEffect effect){
        return new ResourceRepresentation(
                effect.getResourceType().ordinal(),
                effect.getAmount());
    }
}
