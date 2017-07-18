package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
class GameRepresentation {
    private final Long id;
    private final List<PlayerRepresentation> players;
    private final GameStatus status;

    static GameRepresentation fromGame(Game game){
        return new GameRepresentation(game.getId(),
                game.getPlayers().stream().map(PlayerRepresentation::fromPlayer).collect(Collectors.toList()),
                game.getStatus());
    }
}
