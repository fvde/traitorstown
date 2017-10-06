package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@ToString(callSuper = true)
@Getter
public class PartyEffect extends SpecialEffect {

    public static int HOST_REWARD = 3;
    public static int ATTENDEE_REWARD = 3;

    @Builder
    protected PartyEffect() {
        super(Visibility.ALL, 2);
    }

    /***
     * Receive reputation if others attend your party.
     */
    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {
        if (isNew){
            publishMessage(target.getName() + " is throwing a party tomorrow! Inviting all honest citizens...", game.getPlayers());
            game.getLivingPlayers().stream()
                    .filter(p -> p.isNot(target))
                    .filter(Player::hasNoPartyInvitation)
                    .forEach(p -> p.addCard(CardService.Cards.get(CardType.ATTEND_PARTY)));
        } else {
            // get rewarded for every guest
            game.getLivingPlayers().stream()
                    .filter(p -> p.isNot(target) && target.isBeingVisitedForPartyByPlayer(p))
                    .forEach(p -> target.addResource(ResourceType.REPUTATION, HOST_REWARD));
        }
    }

    @Override
    public boolean mayApply(Player origin, Player target) {
        return origin.is(target);
    }
}
