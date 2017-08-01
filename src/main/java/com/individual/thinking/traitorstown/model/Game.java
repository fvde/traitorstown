package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.game.rules.RuleSet;
import com.individual.thinking.traitorstown.model.exceptions.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.deeplearning4j.rl4j.space.Encodable;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.individual.thinking.traitorstown.Configuration.ARRAY_OBSERVATION_SPACE_SIZE;

@Entity
@Builder
@Getter
public class Game implements Encodable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "game_id")
    @NonNull
    private List<Player> players = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "game_id")
    @OrderBy("counter DESC")
    @NonNull
    private List<Turn> turns = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Setter
    @NonNull
    private GameStatus status = GameStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private Role winner;

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void start() throws RuleSetViolationException {
        setStatus(GameStatus.PLAYING);
        Turn firstTurn = Turn.builder().counter(1).build();
        turns.add(firstTurn);
        RuleSet.getRolesForPlayers(players).forEach((role, players) ->
                players.forEach(p -> p.startGameWithRole(role)));
    }

    public void playCard(Player player, Player target, Card card) throws NotCurrentTurnException, PlayerDoesNotHaveCardException, PlayedAlreadyPlayedCardThisTurnException, PlayerMayNotPlayThisCardException, InactiveGameException, TargetPlayerNotInGameException {
        Turn turn = getCurrentTurn().get();

        if (!status.equals(GameStatus.PLAYING)){
            throw new InactiveGameException("This game has not started or is already over!");
        }

        if (!players.contains(target)){
            throw new TargetPlayerNotInGameException("The player you are targeting is not in your game!");
        }

        turn.playCard(card, player, target);

        if (turn.getFinishedPlayers().size() == getPlayers().size()){
            Turn next = turn.startNext();
            if (next == null) {
                status = GameStatus.FINISHED;
                winner = getWinningPlayer().getRole();
            } else {
                turns.add(next);
                players.forEach(Player::drawCard);
            }
        }
    }

    public Integer getReadyPlayers(){
        return players.stream().filter(Player::getReady).collect(Collectors.toList()).size();
    }

    private Player getWinningPlayer(){
        return getCurrentTurn().get().getActiveEffects().stream().filter(EffectActive::isLivingMayor).collect(Collectors.toList()).get(0).getTarget();
    }

    public boolean isReadyToBeStarted(){
        return getReadyPlayers().equals(players.size()) &&
                getReadyPlayers() >= Configuration.MINIMUM_AMOUNT_OF_PLAYERS &&
                status.equals(GameStatus.OPEN);
    }

    public Optional<Turn> getCurrentTurn(){
        return turns.isEmpty() ? Optional.empty() : Optional.of(turns.get(0));
    }

    public boolean isCurrentTurn(Integer turn){
        return getCurrentTurn().isPresent() && getCurrentTurn().get().getCounter().equals(turn);
    }

    @Tolerate
    public Game() {}

    /**
     * ---------------------------------------------
     * This section contains methods required for AI TODO: externalize
     * ---------------------------------------------
     */

    @Override
    public double[] toArray() {
        double[] asArray = new double[]{
                players.size(),
                turns.size(),
                status.ordinal(),
                winner != null ? winner.ordinal() : -1};
        assert (asArray.length == ARRAY_OBSERVATION_SPACE_SIZE);
        return asArray;
    }
}
