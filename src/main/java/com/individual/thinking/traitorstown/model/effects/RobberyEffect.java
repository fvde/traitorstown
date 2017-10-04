package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.Collections;
import java.util.Optional;
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

            publishMessage(
                    Message.builder().content("You were robbed last night! Who could it be?").structure(MessageStructure.CONTENT).build(),
                    game.getId(),
                    Collections.singletonList(target),
                    Optional.of(player),
                    Optional.of(target));

            publishMessage(
                    Message.builder().content(" left the door wide open. The robbery was a success!").structure(MessageStructure.PREFIX_TARGET).build(),
                    game.getId(),
                    Collections.singletonList(player),
                    Optional.of(player),
                    Optional.of(target));

        } else {
            // failure, thief was seen
            publishMessage(
                    Message.builder().content(" tried to rob you last night. He must be a traitor!").structure(MessageStructure.PREFIX_ORIGIN).build(),
                    game.getId(),
                    Collections.singletonList(target),
                    Optional.of(player),
                    Optional.of(target));

            publishMessage(
                    Message.builder().content(" caught you red-handed! They know that you are a traitor...").structure(MessageStructure.PREFIX_TARGET).build(),
                    game.getId(),
                    Collections.singletonList(player),
                    Optional.of(player),
                    Optional.of(target));
        }
    }
}
