package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;

@Entity
@ToString(callSuper = true)
@Getter
public class NotAtHomeEffect extends SpecialEffect {

    @Builder
    protected NotAtHomeEffect(Integer duration, EffectTargetType effectTargetType) {
        super(Visibility.PLAYER, effectTargetType, duration);
    }

    @Tolerate
    NotAtHomeEffect(){ }

    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {
        // no special benefits
    }
}
