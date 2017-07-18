package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.game.exceptions.PlayerNotFoundException;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import com.individual.thinking.traitorstown.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public Game createNewGame() {
        return gameRepository.save(new Game());
    }

    public List<Game> getGamesByStatus(GameStatus status) {
        return gameRepository.findByStatus(status);
    }

    public Game addPlayerToGame(Long id, Player player) throws GameNotFoundException {
        Game game = getGameById(id);
        game.addPlayer(player);
        gameRepository.save(game);
        return game;
    }

    public Game getGameById(Long id) throws GameNotFoundException {
        Game game = gameRepository.findOne(id);

        if (game == null){
            throw new GameNotFoundException("Game not found");
        }

        return game;
    }

    public Player getPlayerbyId(Long id) throws PlayerNotFoundException {
        Player player = playerRepository.findOne(id);

        if (player == null){
            throw new PlayerNotFoundException("Player not found");
        }

        return player;
    }
}
