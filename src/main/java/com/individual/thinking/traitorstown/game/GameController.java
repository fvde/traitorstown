package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @PostMapping("/game")
    public Game createNewGame() throws Exception {
        return gameService.createNewGame();
    }
}
