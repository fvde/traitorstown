package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.game.exceptions.CannotJoinRunningGameException;
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

    protected Game createNewGame() {
        return gameRepository.save(new Game());
    }

    protected List<Game> getGamesByStatus(GameStatus status) {
        return gameRepository.findByStatus(status);
    }

    protected Game addPlayerToGame(Long gameId, Player player) throws GameNotFoundException, CannotJoinRunningGameException {
        Game game = getGameById(gameId);

        if (!game.getStatus().equals(GameStatus.OPEN)){
           throw new CannotJoinRunningGameException("Cannot join a game that has already started");
        }

        game.addPlayer(player);
        gameRepository.save(game);
        return game;
    }

    protected Game removePlayerFromGame(Long gameId, Player player) throws GameNotFoundException {
        Game game = getGameById(gameId);
        game.removePlayer(player);
        gameRepository.save(game);
        return game;
    }

    protected Game getGameById(Long id) throws GameNotFoundException {
        Game game = gameRepository.findOne(id);

        if (game == null){
            throw new GameNotFoundException("Game not found");
        }

        return game;
    }

    protected Game setPlayerReady(Long gameId, Player player, Boolean ready) throws GameNotFoundException {
        Game game = getGameById(gameId);
        player.setReady(ready);
        playerRepository.save(player);
        return startGame(game);
    }

    private Game startGame(Game game){
        if (game.getReadyPlayers().equals(game.getPlayers().size()) && game.getPlayers().size() >= Configuration.MINIMUM_AMOUNT_OF_PLAYERS){
            game.setStatus(GameStatus.PLAYING);
            gameRepository.save(game);
        }
        return game;
    }
}
