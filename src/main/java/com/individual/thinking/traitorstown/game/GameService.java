package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import com.individual.thinking.traitorstown.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    Game createNewGame() {
        return gameRepository.save(new Game());
    }

    List<Game> getGamesByStatus(GameStatus status) {
        return gameRepository.findByStatus(status);
    }

    Game addPlayerToGame(Long gameId, Player player) throws GameNotFoundException {
        Game game = getGameById(gameId);
        game.addPlayer(player);
        gameRepository.save(game);
        return game;
    }

    Game removePlayerFromGame(Long gameId, Player player) throws GameNotFoundException {
        Game game = getGameById(gameId);
        game.removePlayer(player);
        gameRepository.save(game);
        return game;
    }

    Game getGameById(Long id) throws GameNotFoundException {
        Game game = gameRepository.findOne(id);

        if (game == null){
            throw new GameNotFoundException("Game not found");
        }

        return game;
    }
}
