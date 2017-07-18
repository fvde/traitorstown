package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.authorization.AuthorizedPlayer;
import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.game.exceptions.PlayerUnauthorizedException;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping(path = "/games/{gameId}")
    public GameRepresentation getGame(@PathVariable Long gameId) throws GameNotFoundException {
        return GameRepresentation.fromGame(gameService.getGameById(gameId));
    }

    @PostMapping(path = "/games/{gameId}/players")
    public GameRepresentation addPlayer(@PathVariable Long gameId, @RequestBody PlayerVo playerVo, HttpServletRequest request) throws GameNotFoundException, PlayerUnauthorizedException {
        AuthorizedPlayer player = new AuthorizedPlayer(request);
        authorize(player, playerVo.getId());

        return GameRepresentation.fromGame(gameService.addPlayerToGame(gameId, player.getPlayer()));
    }

    @DeleteMapping(path = "/games/{gameId}/players/{playerId}")
    public GameRepresentation removePlayer(@PathVariable Long gameId, @PathVariable Long playerId, HttpServletRequest request) throws GameNotFoundException, PlayerUnauthorizedException {
        AuthorizedPlayer player = new AuthorizedPlayer(request);
        authorize(player, playerId);

        return GameRepresentation.fromGame(gameService.removePlayerFromGame(gameId, player.getPlayer()));
    }

    private void authorize(AuthorizedPlayer player, Long playerId) throws PlayerUnauthorizedException {
        if (player == null || player.getPlayer() == null || !player.getPlayer().getId().equals(playerId)){
            throw new PlayerUnauthorizedException("Each player may only edit their own attributes");
        }
    }
}
