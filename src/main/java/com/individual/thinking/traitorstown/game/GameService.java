package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.game.exceptions.*;
import com.individual.thinking.traitorstown.game.repository.GameRepository;
import com.individual.thinking.traitorstown.game.repository.PlayerRepository;
import com.individual.thinking.traitorstown.game.repository.TurnRepository;
import com.individual.thinking.traitorstown.game.rules.RuleSetViolationException;
import com.individual.thinking.traitorstown.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final TurnRepository turnRepository;

    protected Game createNewGame() {
        return gameRepository.save(Game.builder().build());
    }

    protected List<Game> getGamesByStatus(GameStatus status) {
        return gameRepository.findByStatus(status);
    }

    protected Game addPlayerToGame(Long gameId, Long playerId) throws GameNotFoundException, CannotJoinRunningGameException, PlayerNotFoundException, GameFullException {
        Game game = getGameById(gameId);
        Player player = getPlayerById(playerId);

        if (!game.getStatus().equals(GameStatus.OPEN)){
           throw new CannotJoinRunningGameException("Cannot join a game that has already started");
        }

        if (game.getPlayers().size() >= Configuration.MAXIMUM_AMOUNT_OF_PLAYERS){
            throw new GameFullException("This game is already full");
        }

        game.addPlayer(player);
        gameRepository.save(game);
        return game;
    }

    protected Game removePlayerFromGame(Long gameId, Long playerId) throws GameNotFoundException, PlayerNotFoundException {
        Game game = getGameById(gameId);
        Player player = getPlayerById(playerId);
        game.removePlayer(player);
        gameRepository.save(game);
        return game;
    }

    protected Game setPlayerReady(Long gameId, Long playerId, Boolean ready) throws GameNotFoundException, PlayerNotFoundException, RuleSetViolationException {
        Game game = getGameById(gameId);
        Player player = getPlayerById(playerId);
        player.setReady(ready);
        playerRepository.save(player);

        if (game.getReadyPlayers().equals(game.getPlayers().size()) && game.getPlayers().size() >= Configuration.MINIMUM_AMOUNT_OF_PLAYERS) {
            game = startGame(game);
        }

        return game;
    }

    private Game startGame(Game game) throws RuleSetViolationException {
        game.start();
        gameRepository.save(game);
        return game;
    }

    protected List<Card> getPlayerCards(Long playerId) throws PlayerNotFoundException {
        Player player = getPlayerById(playerId);
        return player.getCards();
    }

    protected Player getPlayerById(Long id) throws PlayerNotFoundException {
        Player player = playerRepository.findOne(id);
        if (player == null){
            throw new PlayerNotFoundException("Game not found");
        }
        return player;
    }

    protected Game getGameById(Long id) throws GameNotFoundException {
        Game game = gameRepository.findOne(id);
        if (game == null){
            throw new GameNotFoundException("Game not found");
        }
        return game;
    }

    public Turn getTurnByGameIdAndCounter(Long gameId, Integer counter) throws TurnNotFoundException {
        Optional<Turn> turn = turnRepository.findByGameIdAndCounter(gameId, counter);
        if (!turn.isPresent()){
            throw new TurnNotFoundException("Turn not found");
        }
        return turn.get();
    }
}
