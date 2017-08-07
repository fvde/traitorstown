package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.Configuration;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@Getter
@ToString
public class Effect {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Builder.Default
    private EffectType effectType = EffectType.OTHER;

    @Enumerated(EnumType.STRING)
    @NonNull
    private EffectOperator operator;

    @Enumerated(EnumType.STRING)
    @NonNull
    private EffectTargetType effectTargetType;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Resource targetResource;

    @NonNull
    private Integer amount;

    @NonNull
    private Integer duration;

    @NonNull
    @Builder.Default
    private EffectVisibility visibility = EffectVisibility.PLAYER;

    @Tolerate
    Effect() {}

    public void apply(Player player, Player target) {
        if (operator == EffectOperator.REMOVE){
            player.setResource(targetResource, operator.apply(target.getResource(targetResource), amount));
        } else {
            target.setResource(targetResource, operator.apply(target.getResource(targetResource), amount));
        }
    }

    public boolean mayApply(Player player){
        if (operator.equals(EffectOperator.REMOVE)){
            return player.getResource(targetResource) - amount >= Configuration.MINIMUM_RESOURCES.get(targetResource);
        }

        return true;
    }

    public boolean isCost(){
        return operator.equals(EffectOperator.REMOVE);
    }

    public String getName() {
        return operator + " " + amount + " " + targetResource;
    }
}
