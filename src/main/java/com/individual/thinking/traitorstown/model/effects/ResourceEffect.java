package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.ResourceType;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@ToString(callSuper = true)
@Getter
public class ResourceEffect extends Effect {

    @Enumerated(EnumType.STRING)
    private EffectOperator operator;

    @Enumerated(EnumType.STRING)
    private EffectTargetType effectTargetType;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private Integer amount;

    @Builder
    protected ResourceEffect(Visibility visibility,  Integer duration, EffectOperator operator, EffectTargetType effectTargetType, ResourceType resourceType, Integer amount) {
        super(visibility == null ? Visibility.PLAYER : visibility,
                duration);
        this.operator = operator;
        this.effectTargetType = effectTargetType;
        this.resourceType = resourceType;
        this.amount = amount;
    }

    @Tolerate
    ResourceEffect(){
        // hibernate
    }

    @Override
    public void apply(Player player, Player target) {
        if (operator == EffectOperator.REMOVE) {
            if (effectTargetType == EffectTargetType.SELF) {
                player.removeResource(resourceType, amount);
            } else if (effectTargetType == EffectTargetType.TARGET) {
                target.removeResource(resourceType, amount);
            } else {
                throw new RuntimeException("Unknown effect target type!");
            }
        } else {
            if (effectTargetType == EffectTargetType.SELF) {
                player.addResource(resourceType, amount);
            } else if (effectTargetType == EffectTargetType.TARGET) {
                target.addResource(resourceType, amount);
            } else {
                throw new RuntimeException("Unknown effect target type!");
            }
        }
    }

    @Override
    public boolean mayApply(Player target) {
        if (operator.equals(EffectOperator.REMOVE)){
            return target.getResource(resourceType) - amount >= Configuration.MINIMUM_RESOURCES.get(resourceType);
        }

        return true;
    }

    @Override
    public boolean isCost() {
        return false;
    }

    @Override
    public String getName() {
        return operator + " " + amount + " " + resourceType;
    }
}
