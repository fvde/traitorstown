package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.ResourceType;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@ToString(callSuper = true)
@Getter
public class RobberyEffect extends SpecialEffect {

    @Builder
    protected RobberyEffect() {
        super(Visibility.PLAYER, 1);
    }

    /**
     * Robs the target player and converts their gold to stolen gold.
     * The effect is significantly more successful if the target is not at home.
     * @param game
     * @param player
     * @param target
     * @param isNew
     */
    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {

        double successRate = 0.2f;
        if (target.isNotAtHome()){
            successRate = 0.9f;
        }

        if (ThreadLocalRandom.current().nextFloat() <= successRate){
            // successful robbery
            player.addResource(ResourceType.STOLEN_GOLD, target.getResource(ResourceType.GOLD));
            target.removeResource(ResourceType.GOLD, target.getResource(ResourceType.GOLD));

            publishMessage("You were robbed last night! Who could it be?", Collections.singletonList(target));
            publishMessage(target.getName() +" left the door wide open. The robbery was a success!", Collections.singletonList(player));

        } else {
            // failure, thief was seen
            publishMessage(player.getName() + " tried to rob you last night. He must be a traitor!", Collections.singletonList(target));
            publishMessage(target.getName() + " caught you red-handed! They know that you are a traitor...", Collections.singletonList(player));
        }
    }

    @Override
    public boolean mayApply(Player origin, Player target) {
        return !origin.getId().equals(target.getId());
    }
}
