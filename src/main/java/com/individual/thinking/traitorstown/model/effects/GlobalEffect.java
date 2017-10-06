package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;

@Entity
@ToString(callSuper = true)
@Getter
public abstract class GlobalEffect extends Effect {

    protected GlobalEffect(Integer duration) {
        this(Visibility.NONE, EffectTargetType.SELF, duration);
    }

    protected GlobalEffect(Visibility visibility, EffectTargetType effectTargetType, Integer duration) {
        super(visibility == null ? Visibility.PLAYER : visibility,
                effectTargetType == null ? EffectTargetType.TARGET : effectTargetType,
                duration == null ? Integer.MAX_VALUE : duration);
    }

    @Tolerate
    GlobalEffect(){ }

    @Override
    public void apply(Game game, Player origin, Player target, boolean isNew) {
        apply(game);
    }

    public ActiveGameEffect toActiveGameEffect(){
        return ActiveGameEffect.builder()
                .effect(this)
                .remainingTurns(getDuration())
                .build();
    }

    public abstract void apply(Game game);

    @Override
    public boolean mayApply(Player origin, Player target) {
        return true;
    }

    @Override
    public boolean isCost() {
        return false;
    }

    @Override
    public String getName() {
        return "Global Effect " + getClass().getSimpleName().replaceAll("Effect", "");
    }
}
