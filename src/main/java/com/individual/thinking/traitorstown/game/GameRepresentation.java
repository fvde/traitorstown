package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import lombok.Data;

@Data
class GameRepresentation {
    private final Long id;
    private final Integer players;
    private final Integer playersReady;
    private final GameStatus status;

    static GameRepresentation fromGame(Game game){
        return new GameRepresentation(game.getId(),
                game.getPlayers().size(),
                game.getReadyPlayers(),
                game.getStatus());
    }
}
