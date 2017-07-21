package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.game.rules.RuleSet;
import com.individual.thinking.traitorstown.model.exceptions.AlreadyPlayedCardThisTurnException;
import com.individual.thinking.traitorstown.model.exceptions.NotCurrentTurnException;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import com.individual.thinking.traitorstown.model.exceptions.RuleSetViolationException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
public class Game {

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

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void start() throws RuleSetViolationException {
        setStatus(GameStatus.PLAYING);
        turns.add(Turn.builder().counter(1).build());
        RuleSet.getRolesForPlayers(players).forEach((role, players) ->
                players.forEach(p -> p.setRole(role)));
        players.forEach(p -> p.drawCards(Configuration.INITIAL_AMOUNT_OF_CARDS));
    }

    public void playCard(Player player, Card card, Integer turnCounter) throws NotCurrentTurnException, PlayerDoesNotHaveCardException, AlreadyPlayedCardThisTurnException {
        Turn turn = getCurrentTurn();

        if (!isCurrentTurn(turnCounter)){
            throw new NotCurrentTurnException("It is currently not turn " + turn);
        }

        player.playCard(card);
        turn.playCard(card, player);

        if (turn.getFinishedPlayers().size() == getPlayers().size()){
            players.forEach(Player::drawCard);
            turns.add(turn.startNext());
        }
    }

    public Integer getReadyPlayers(){
        return players.stream().filter(Player::getReady).collect(Collectors.toList()).size();
    }

    public boolean isReadyToBeStarted(){
        return getReadyPlayers().equals(players.size()) &&
                getReadyPlayers() >= Configuration.MINIMUM_AMOUNT_OF_PLAYERS &&
                status.equals(GameStatus.OPEN);
    }

    private Turn getCurrentTurn(){
        return turns.get(0);
    }

    private boolean isCurrentTurn(Integer turn){
        return !turns.isEmpty() && getCurrentTurn().getCounter().equals(turn);
    }

    @Tolerate
    public Game() {}
}
