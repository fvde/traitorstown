package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.ResourceType;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@ToString(callSuper = true)
@Getter
public class AttendPartyEffect extends SpecialEffect {

    @Builder
    protected AttendPartyEffect() {
        super(Visibility.ALL,1);
    }

    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {
        publishMessage(player.getName() + " attended " + target.getName() + "'s party!", game.getPlayers());
        player.addResource(ResourceType.REPUTATION, PartyEffect.ATTENDEE_REWARD);
    }

    @Override
    public boolean mayApply(Player origin, Player target) {
        return target.isHostingAParty() && target.isNot(origin);
    }
}
