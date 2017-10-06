package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.model.CardType;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Turn;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@ToString(callSuper = true)
@Getter
public class ElectionsEffect extends GlobalEffect {

    @Builder
    protected ElectionsEffect() {
        super(Integer.MAX_VALUE);
    }

    @Override
    public void apply(Game game) {
        Turn turn = game.getCurrentTurn();
        if (turn.isDayBeforeElections() && game.getPlayers().stream().filter(Player::isCandidate).count() > 0){
            game.getLivingPlayers().stream().forEach(player -> player.addCard(CardService.Cards.get(CardType.VOTE)));
            publishMessage("Today's election day. Vote for someone you trust!", game.getPlayers());
        }

        if (turn.isElectionDay() && game.getPlayers().stream().filter(Player::isCandidate).count() > 0){
            publishMessage("And the results are in...", game.getPlayers());
            final Map<Player, Long> votes = game.getPlayers().stream()
                    .filter(Player::isCandidate)
                    .collect(Collectors.toMap(p -> p, Player::getVotes));

            votes.forEach((player, voting) -> publishMessage("Candidate " + player.getName() + ": " + voting + " votes.", game.getPlayers()));


            boolean tie = false;
            Long highestVoting = 0L;
            Player electedPlayer = null;

            for (Player candidate : votes.keySet()){
                Long voting = votes.get(candidate);
                if (voting > highestVoting){
                    highestVoting = voting;
                    electedPlayer = candidate;
                    tie = false;
                } else if (voting == highestVoting){
                    tie = true;
                }
            }

            if (highestVoting == 0){
                publishMessage("Looks like nobody can be trusted... :(!", game.getPlayers());
            }

            if (tie) {
                publishMessage("It's a tie! :(!", game.getPlayers());
            }

            if (electedPlayer != null && !tie){
                // TODO add mayor cards
                electedPlayer.addEffect(CardService.Effects.get(SpecialEffectType.MAYOR), electedPlayer);
                publishMessage(electedPlayer.getName() +" was elected mayor!", game.getPlayers());
            }
            // clean up candidacies
            game.getPlayers().stream().forEach(Player::clearCandidacy);
        }
    }
}
