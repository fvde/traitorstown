package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Player;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PostTurnEffect {
    final Effect effect;
    final Player origin;
    final Player target;
}
