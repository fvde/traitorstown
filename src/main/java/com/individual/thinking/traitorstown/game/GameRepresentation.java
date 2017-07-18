package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import lombok.Data;

import java.util.stream.Collectors;

@Data
class GameRepresentation {
    private final Long id;
    private final Integer players;
    private final Integer playersReady;
    private final GameStatus status;

    static GameRepresentation fromGame(Game game){
        return new GameRepresentation(game.getId(),
                game.getPlayers().size(),
                game.getPlayers().stream().filter(p -> p.getReady()).collect(Collectors.toList()).size(),
                game.getStatus());
    }
}
