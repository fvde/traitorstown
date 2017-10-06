package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.model.CardType;
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
public class TrialEffect extends SpecialEffect {

    @Builder
    protected TrialEffect() {
        super(Visibility.ALL, 2);
    }

    /**
     * Puts target on trial the first day, next day votes are counted and the target may be killed.
     */
    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {
        if (isNew){
            publishMessage("For his crimes against the town " + target.getName() +  " has been put on trial by Mayor " + player.getName(), game.getPlayers());
            game.getLivingPlayers().stream().forEach(p -> p.addCard(CardService.Cards.get(CardType.VOTE_KILL)));
            game.getLivingPlayers().stream().forEach(p -> p.addCard(CardService.Cards.get(CardType.VOTE_SPARE)));
        } else {
            long spareVotes = target.getSpareVotes();
            long killVotes = target.getKillVotes();

            publishMessage(spareVotes +  " honorable citizens voted to spare " + target.getName(), game.getPlayers());
            publishMessage(killVotes +  " honorable citizens voted to kill " + target.getName(), game.getPlayers());

            if (killVotes > spareVotes){
                game.addPostTurnEffect(CardService.Effects.get(SpecialEffectType.DEATH), player, target);
                publishMessage(target.getName() +  " is hereby sentenced to death, to be executed immediately.", game.getPlayers());
            } else {
                publishMessage(target.getName() +  " has been deemed innocent.", game.getPlayers());
            }
        }
    }

    @Override
    public boolean mayApply(Player origin, Player target) {
        return origin.isNot(target);
    }
}
