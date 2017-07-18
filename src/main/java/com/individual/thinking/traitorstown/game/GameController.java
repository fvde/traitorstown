package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @PostMapping("/games")
    public Game createNewGame() throws Exception {
        return gameService.createNewGame();
    }

    @GetMapping("/games")
    public List<GameRepresentation> getGames(@RequestParam GameStatus status) {
        return gameService.getGamesByStatus(status).stream().map(GameRepresentation::fromGame).collect(Collectors.toList());
    }

    @GetMapping(path = "/games/{id}")
    public GameRepresentation getGame(@PathVariable Long id) throws GameNotFoundException {
        return GameRepresentation.fromGame(gameService.getGameById(id));
    }

}
