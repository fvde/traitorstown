package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Game createNewGame() {
        return gameRepository.save(new Game());
    }
}
