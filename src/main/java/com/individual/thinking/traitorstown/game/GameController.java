package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.authorization.AuthorizedPlayer;
import com.individual.thinking.traitorstown.game.exceptions.*;
import com.individual.thinking.traitorstown.game.representation.CardRepresentation;
import com.individual.thinking.traitorstown.game.representation.GameRepresentation;
import com.individual.thinking.traitorstown.game.representation.TurnRepresentation;
import com.individual.thinking.traitorstown.game.rules.RuleSetViolationException;
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
    public GameRepresentation createNewGame() throws Exception {
        return GameRepresentation.fromGame(gameService.createNewGame());
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
    public GameRepresentation addPlayer(@PathVariable Long gameId, @RequestBody PlayerVo playerVo, HttpServletRequest request) throws GameNotFoundException, PlayerUnauthorizedException, CannotJoinRunningGameException, PlayerNotFoundException, GameFullException {
        AuthorizedPlayer player = new AuthorizedPlayer(request).authorize(null, playerVo.getId());
        return GameRepresentation.fromGame(gameService.addPlayerToGame(gameId, player.getPlayer().getId()));
    }

    @DeleteMapping(path = "/games/{gameId}/players/{playerId}")
    public GameRepresentation removePlayer(@PathVariable Long gameId, @PathVariable Long playerId, HttpServletRequest request) throws GameNotFoundException, PlayerUnauthorizedException, PlayerNotFoundException {
        AuthorizedPlayer player = new AuthorizedPlayer(request).authorize(gameId, playerId);
        return GameRepresentation.fromGame(gameService.removePlayerFromGame(gameId, player.getPlayer().getId()));
    }

    @PutMapping(path = "/games/{gameId}/players/{playerId}")
    public GameRepresentation setPlayerReady(@PathVariable Long gameId, @PathVariable Long playerId, @RequestBody PlayerReadyVo playerReadyVo, HttpServletRequest request) throws GameNotFoundException, PlayerUnauthorizedException, PlayerNotFoundException, RuleSetViolationException {
        AuthorizedPlayer player = new AuthorizedPlayer(request).authorize(gameId, playerId);
        return GameRepresentation.fromGame(gameService.setPlayerReady(gameId, player.getPlayer().getId(), playerReadyVo.getReady()));
    }

    @GetMapping(path = "/games/{gameId}/players/{playerId}/cards")
    public List<CardRepresentation> getPlayerCards(@PathVariable Long gameId, @PathVariable Long playerId, HttpServletRequest request) throws GameNotFoundException, PlayerUnauthorizedException, PlayerNotFoundException {
        AuthorizedPlayer player = new AuthorizedPlayer(request).authorize(gameId, playerId);
        return gameService.getPlayerCards(player.getPlayer().getId()).stream().map(CardRepresentation::fromcard).collect(Collectors.toList());
    }

    @GetMapping(path = "/games/{gameId}/turns/{turnCounter}")
    public TurnRepresentation getTurn(@PathVariable Long gameId, @PathVariable Integer turnCounter, HttpServletRequest request) throws PlayerUnauthorizedException, TurnNotFoundException {
        new AuthorizedPlayer(request).authorize(gameId, null);
        return TurnRepresentation.fromTurn(gameService.getTurnByGameIdAndCounter(gameId, turnCounter));
    }
}
