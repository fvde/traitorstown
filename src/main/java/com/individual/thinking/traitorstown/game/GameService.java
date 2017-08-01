package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.game.exceptions.*;
import com.individual.thinking.traitorstown.game.repository.GameRepository;
import com.individual.thinking.traitorstown.game.repository.PlayerRepository;
import com.individual.thinking.traitorstown.game.repository.TurnRepository;
import com.individual.thinking.traitorstown.model.exceptions.*;
import com.individual.thinking.traitorstown.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final TurnRepository turnRepository;
    private final CardService cardService;

    public Game createNewGame() {
        return gameRepository.save(Game.builder()
                .players(new ArrayList<>())
                .turns(new ArrayList<>())
                .status(GameStatus.OPEN)
                .build());
    }

    public List<Game> getGamesByStatus(GameStatus status) {
        return gameRepository.findByStatus(status);
    }

    public Game addPlayerToGame(Long gameId, Long playerId) throws GameNotFoundException, CannotJoinRunningGameException, PlayerNotFoundException, GameFullException, AlreadyInGameException, RuleSetViolationException {
        Game game = getGameById(gameId);

        if (!game.getStatus().equals(GameStatus.OPEN)){
           throw new CannotJoinRunningGameException("Cannot join a game that has already started");
        }

        if (game.getPlayers().size() >= Configuration.MAXIMUM_AMOUNT_OF_PLAYERS){
            throw new GameFullException("This game is already full");
        }

        Player player = getPlayerById(playerId);

        if (game.getPlayers().contains(player)){
            throw new AlreadyInGameException("Already in this game");
        }

        game.addPlayer(player);
        player.setReady(true);

        if (game.isReadyToBeStarted()) {
            game = startGame(game);
        }

        gameRepository.save(game);
        return game;
    }

    public Game removePlayerFromGame(Long gameId, Long playerId) throws GameNotFoundException, PlayerNotFoundException, CannotLeaveRunningGameException {
        Game game = getGameById(gameId);

        if (!game.getStatus().equals(GameStatus.OPEN)){
            throw new CannotLeaveRunningGameException("You can not leave a running game");
        }

        game.removePlayer(getPlayerById(playerId));
        gameRepository.save(game);
        return game;
    }

    public Game setPlayerReady(Long gameId, Long playerId, Boolean ready) throws GameNotFoundException, PlayerNotFoundException, RuleSetViolationException {
        Game game = getGameById(gameId);
        Player player = getPlayerById(playerId);
        player.setReady(ready);
        playerRepository.save(player);

        if (game.isReadyToBeStarted()) {
            game = startGame(game);
        }

        return game;
    }

    public void playCard(Long gameId, Integer turn, Long cardId, Long playerId, Long targetPlayerId) throws GameNotFoundException, CardNotFoundException, PlayerNotFoundException, NotCurrentTurnException, PlayerDoesNotHaveCardException, PlayedAlreadyPlayedCardThisTurnException, PlayerMayNotPlayThisCardException, InactiveGameException, TargetPlayerNotInGameException {
        Game game = getGameById(gameId);

        if (!game.isCurrentTurn(turn)){
            throw new NotCurrentTurnException("It is currently not turn " + turn);
        }

        game.playCard(
                getPlayerById(playerId),
                getPlayerById(targetPlayerId),
                cardService.getCardById(cardId));
        gameRepository.save(game);
    }

    private Game startGame(Game game) throws RuleSetViolationException {
        game.start();
        return gameRepository.save(game);
    }

    protected List<Card> getPlayerCards(Long playerId) throws PlayerNotFoundException {
        return getPlayerById(playerId).getHandCards();
    }

    public Player getPlayerById(Long id) throws PlayerNotFoundException {
        Player player = playerRepository.findOne(id);
        if (player == null){
            throw new PlayerNotFoundException("Player not found");
        }
        return player;
    }

    public Game getGameById(Long id) throws GameNotFoundException {
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

    public Game getGameByPlayerId(Long playerId) throws PlayerNotInGameException, PlayerNotFoundException, GameNotFoundException {
        Player player = getPlayerById(playerId);
        if (player.getGameId() == null){
            throw new PlayerNotInGameException("Currently not playing a game");
        }
        return getGameById(player.getGameId());
    }
}
