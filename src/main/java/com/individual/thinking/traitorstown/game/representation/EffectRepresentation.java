package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.EffectActive;
import lombok.Data;

@Data
public class EffectRepresentation {
    private final String name;
    private final int remainingTurns;

    public static EffectRepresentation fromActiveEffect(EffectActive activeEffect){
        return new EffectRepresentation(
                activeEffect.getEffect().getName(),
                activeEffect.getRemainingTurns());
    }
}
