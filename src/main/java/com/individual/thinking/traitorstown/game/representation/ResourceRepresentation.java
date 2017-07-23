package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Effect;
import com.individual.thinking.traitorstown.model.Resource;
import lombok.Data;

@Data
public class ResourceRepresentation {
    private final Resource type;
    private final Integer amount;

    public static ResourceRepresentation fromEffect(Effect effect){
        return new ResourceRepresentation(
                effect.getTargetType(),
                effect.getAmount());
    }
}
