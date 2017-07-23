package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Effect;
import com.individual.thinking.traitorstown.model.Resource;
import lombok.Data;

@Data
public class CardCostRepresentation {
    private final Resource resource;
    private final Integer amount;

    public static CardCostRepresentation fromEffect(Effect effect){
        return new CardCostRepresentation(
                effect.getTargetType(),
                effect.getAmount());
    }
}
