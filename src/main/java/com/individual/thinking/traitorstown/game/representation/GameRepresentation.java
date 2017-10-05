package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import com.individual.thinking.traitorstown.model.Player;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GameRepresentation {
    private final Long id;
    private final List<PlayerRepresentation> players;
    private final int status;
    private final Integer turn;
    private final int winner;

    public static GameRepresentation fromGame(Game game, Player asPlayer){
        return new GameRepresentation(game.getId(),
                game.getPlayers().stream().map(player -> PlayerRepresentation.fromPlayer(player, asPlayer)).collect(Collectors.toList()),
                game.getStatus().ordinal(),
                game.getStatus().equals(GameStatus.PLAYING) ? game.getCurrentTurn().getCounter() : 0,
                game.getWinner().ordinal());
    }
}
