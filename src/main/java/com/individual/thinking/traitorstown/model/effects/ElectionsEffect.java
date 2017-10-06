package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.model.*;
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

            Voting voting = new Voting(votes);

            if (voting.getNoVotesWereCast()){
                publishMessage("Looks like nobody can be trusted... :(!", game.getPlayers());
            }

            if (voting.getEndedWithTie()) {
                publishMessage("It's a tie! :(!", game.getPlayers());
            }

            if (voting.getWinner() != null){
                Player winner = voting.getWinner();
                winner.addEffect(CardService.Effects.get(SpecialEffectType.MAYOR), winner);
                publishMessage(winner.getName() +" was elected mayor!", game.getPlayers());
            }
            // clean up candidacies
            game.getPlayers().stream().forEach(Player::clearCandidacy);
        }
    }
}
