package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Game;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GameRepresentation {
    private final Long id;
    private final List<PlayerRepresentation> players;
    private final int status;
    private final Integer turn;

    public static GameRepresentation fromGame(Game game){
        return new GameRepresentation(game.getId(),
                game.getPlayers().stream().map(PlayerRepresentation::fromPlayer).collect(Collectors.toList()),
                game.getStatus().ordinal(),
                game.getCurrentTurn().isPresent() ? game.getCurrentTurn().get().getCounter() : 0);
    }
}
