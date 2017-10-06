package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
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
public class MurderEffect extends SpecialEffect {

    @Builder
    protected MurderEffect() {
        super(Visibility.PLAYER, 1);
    }

    /**
     * Murders the target. Fails when target is not at home.
     */
    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {

        double successRate = 0.7f;

        if (target.isNotAtHome()){
            publishMessage("The attempted murder of " + target.getName() +  " failed because the target was not at home! At least they didn't notice...", Collections.singletonList(player));
            return;
        }

        if (ThreadLocalRandom.current().nextFloat() <= successRate){
            // successful murder
            game.addPostTurnEffect(CardService.Effects.get(SpecialEffectType.DEATH), player, target);
            publishMessage(target.getName() + " was brutally murdered last night!", game.getPlayers());

        } else {
            publishMessage("Someone tried to murder " + target.getName() + " last night, but the attack failed!", game.getPlayers());
        }
    }

    @Override
    public boolean mayApply(Player origin, Player target) {
        return origin.isNot(target);
    }
}
