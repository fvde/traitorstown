package com.individual.thinking.traitorstown.ai.learning.model;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import lombok.Value;
import org.deeplearning4j.rl4j.space.Encodable;

import java.util.List;
import java.util.stream.Collectors;

import static com.individual.thinking.traitorstown.Configuration.TOTAL_NUMBER_OF_CARDS;

@Value
public class GameState implements Encodable {

    public static Integer GAME_STATE_GENERAL_INFORMATION_SIZE = 2;

    private final Integer numberOfPlayingPlayers;
    private final Integer playerRole;
    @Deprecated
    private final Integer winner;
    private final List<Long> cards;

    public static GameState fromGameAndPlayer(Game game, long playingPlayerId){
        Player player = game.getPlayers().stream().filter(p -> p.getId().equals(playingPlayerId)).findFirst().get();
        return new GameState(
                game.getPlayers().size(),
                player.getRole().ordinal(),
                game.getWinner().ordinal(),
                player.getHandCards().stream().map(card -> card.getId()).collect(Collectors.toList()));
    }

    @Override
    public double[] toArray() {
        double[] array = new double[GAME_STATE_GENERAL_INFORMATION_SIZE + TOTAL_NUMBER_OF_CARDS];
        int index = 0;

        // General
        array[index++] = numberOfPlayingPlayers;
        array[index++] = playerRole;

        // Cards
        cards.stream().forEach(card -> {
            array[GAME_STATE_GENERAL_INFORMATION_SIZE + card.intValue() - 1] = 1;
        });

        assert (array.length == Configuration.ARRAY_OBSERVATION_SPACE_SIZE);
        return array;
    }

    public boolean isWinner(){
        return playerRole.equals(winner);
    }
}
