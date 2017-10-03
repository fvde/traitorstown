package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Getter
@ToString
public abstract class Effect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    private EffectTargetType effectTargetType;

    private Integer duration;

    Effect(Visibility visibility, EffectTargetType effectTargetType, Integer duration){
        this.visibility = visibility;
        this.effectTargetType = effectTargetType;
        this.duration = duration;
    }

    @Tolerate
    Effect(){
        // for hibernate
    }

    public abstract void apply(Game game, Player origin, Player target);
    public abstract boolean mayApply(Player target);
    public abstract boolean isCost();
    public abstract String getName();
    public boolean isOfType(Class<? extends Effect> type) {
        return getClass() == type;
    }
}
