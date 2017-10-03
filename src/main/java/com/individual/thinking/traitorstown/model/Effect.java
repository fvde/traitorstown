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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private ResourceType resourceType;

    @NonNull
    private Integer amount;

    @NonNull
    private Integer duration;

    @NonNull
    @Builder.Default
    private Visibility visibility = Visibility.PLAYER;

    @Tolerate
    Effect() {}

    public void apply(Player player, Player target) {
        if (operator == EffectOperator.REMOVE) {
            if (effectTargetType == EffectTargetType.SELF) {
                player.removeResource(resourceType, amount);
            } else {
                target.removeResource(resourceType, amount);
            }
        } else {
            if (effectTargetType == EffectTargetType.SELF) {
                player.addResource(resourceType, amount);
            } else {
                target.addResource(resourceType, amount);
            }
        }
    }

    public boolean mayApply(Player player){
        if (operator.equals(EffectOperator.REMOVE)){
            return player.getResource(resourceType) - amount >= Configuration.MINIMUM_RESOURCES.get(resourceType);
        }

        return true;
    }

    public boolean isCost(){
        return operator.equals(EffectOperator.REMOVE);
    }

    public String getName() {
        return operator + " " + amount + " " + resourceType;
    }
}
