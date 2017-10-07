package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.game.rules.Rules;
import com.individual.thinking.traitorstown.model.Game;
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
    private ResourceType resourceType;

    private Integer amount;

    @Builder
    protected ResourceEffect(Visibility visibility, EffectTargetType effectTargetType, Integer duration, EffectOperator operator, ResourceType resourceType, Integer amount) {
        super(visibility == null ? Visibility.PLAYER : visibility,
                effectTargetType, duration);
        this.operator = operator;
        this.resourceType = resourceType;
        this.amount = amount;
    }

    @Tolerate
    ResourceEffect(){
        // hibernate
    }

    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {
        if (operator == EffectOperator.REMOVE) {
            target.removeResource(resourceType, amount);
        } else {
            target.addResource(resourceType, amount);
        }
    }

    @Override
    public boolean mayApply(Player origin, Player target) {
        if (operator.equals(EffectOperator.REMOVE) && resourceType.equals(ResourceType.GOLD)){
            int currentGold = getEffectTargetType().equals(EffectTargetType.SELF)
                    ? origin.getResource(ResourceType.GOLD) + origin.getResource(ResourceType.STOLEN_GOLD)
                    : target.getResource(ResourceType.GOLD) + target.getResource(ResourceType.STOLEN_GOLD);

            return currentGold - amount >= Rules.MINIMUM_RESOURCES.get(resourceType);
        } else if (operator.equals(EffectOperator.REMOVE)){
            int currentAmount = getEffectTargetType().equals(EffectTargetType.SELF)
                    ? origin.getResource(resourceType)
                    : target.getResource(resourceType);
            return currentAmount - amount >= Rules.MINIMUM_RESOURCES.get(resourceType);
        }

        return true;
    }

    @Override
    public boolean isCost() {
        return false;
    }

    @Override
    public String getName() {
        return operator.toString().toLowerCase() + " " + amount + " " + resourceType.toString().toLowerCase();
    }
}
