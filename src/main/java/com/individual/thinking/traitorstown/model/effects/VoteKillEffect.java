package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@ToString(callSuper = true)
@Getter
public class VoteKillEffect extends SpecialEffect {

    @Builder
    protected VoteKillEffect() {
        super(Visibility.ALL, 1);
    }

    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {
        publishMessage(player.getName() + " voted to kill " + target.getName() + "!", game.getPlayers());
    }

    @Override
    public boolean mayApply(Player origin, Player target) {
        return target.isOnTrial();
    }
}
