package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Game createNewGame() {
        return gameRepository.save(new Game());
    }

    public List<Game> getGamesByStatus(GameStatus status) {
        return gameRepository.findByStatus(status);
    }

    public Game getGameById(Long id) throws GameNotFoundException {
        Game game = gameRepository.findOne(id);

        if (game == null){
            throw new GameNotFoundException("Game not found");
        }

        return game;
    }
}
