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

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.individual.thinking.traitorstown.Configuration.ARRAY_OBSERVATION_SPACE_SIZE;
import static java.util.Collections.singletonList;

@Entity
@Builder
@Getter
public class Game implements Encodable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    @JoinColumn(name = "game_id")
    @NonNull
    private List<Player> players = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
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

    public void playCard(Player player, Player target, Card card, Integer turnCounter) throws NotCurrentTurnException, PlayerDoesNotHaveCardException, PlayedAlreadyPlayedCardThisTurnException, PlayerMayNotPlayThisCardException, InactiveGameException {
        Turn turn = getCurrentTurn().get();

        if (!status.equals(GameStatus.PLAYING)){
            throw new InactiveGameException("This game has not started or is already over!");
        }

        if (!isCurrentTurn(turnCounter)){
            throw new NotCurrentTurnException("It is currently not turn " + turn);
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

    private boolean isCurrentTurn(Integer turn){
        return getCurrentTurn().isPresent() && getCurrentTurn().get().getCounter().equals(turn);
    }

    @Tolerate
    public Game() {}

    /**
     * ---------------------------------------------
     * This section contains methods required for AI TODO: externalize
     * ---------------------------------------------
     */

    public static Game buildAITrainingGame() {
        return Game.builder()
                .turns(new ArrayList<>())
                .players(singletonList(Player.builder().ready(true).build()))
                .status(GameStatus.PLAYING)
                .turns(Collections.emptyList())
                .build();
    }

    public void playCardAsPlayerFromCardSlotTargetingPlayer(int playerIndex, int cardIndex, int targetPlayer){
        try {
            playCard(players.get(playerIndex), players.get(targetPlayer), players.get(playerIndex).getHandCards().get(cardIndex), getCurrentTurn().get().getCounter());
        } catch (NotCurrentTurnException e) {
            e.printStackTrace();
        } catch (PlayerDoesNotHaveCardException e) {
            e.printStackTrace();
        } catch (PlayedAlreadyPlayedCardThisTurnException e) {
            e.printStackTrace();
        } catch (PlayerMayNotPlayThisCardException e) {
            e.printStackTrace();
        } catch (InactiveGameException e) {
            e.printStackTrace();
        }
    }

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
