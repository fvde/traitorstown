package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.model.effects.Effect;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PostTurnEffect {
    final Effect effect;
    final Player origin;
    final Player target;
}
