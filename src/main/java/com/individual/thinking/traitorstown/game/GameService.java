package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.ArtificialIntelligenceService;
import com.individual.thinking.traitorstown.ai.learning.model.Action;
import com.individual.thinking.traitorstown.game.exceptions.*;
import com.individual.thinking.traitorstown.game.repository.GameRepository;
import com.individual.thinking.traitorstown.game.repository.TurnRepository;
import com.individual.thinking.traitorstown.message.MessageService;
import com.individual.thinking.traitorstown.model.*;
import com.individual.thinking.traitorstown.model.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final TurnRepository turnRepository;
    private final CardService cardService;
    private final ArtificialIntelligenceService artificialIntelligenceService;
    private final MessageService messageService;
    private final TraitorsTownConfiguration configuration;
    private final PlayerService playerService;

    public Game createNewGame() {
        return gameRepository.save(Game.builder()
                .players(new ArrayList<>())
                .desiredNumberOfHumanPlayers(configuration.getMinimumNumberOfPlayers())
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

        if (game.getPlayers().size() >= configuration.getMaximumNumberOfPlayers()){
            throw new GameFullException("This game is already full");
        }

        Player player = playerService.getPlayerById(playerId);

        if (game.getPlayers().contains(player)){
            throw new AlreadyInGameException("Already in this game");
        }

        game.addPlayer(player);
        playerService.setPlayerReady(playerId, true);

        if (game.isReadyToBeStarted()) {
            game = startGame(game);
        }

        gameRepository.save(game);
        messageService.sendMessageToGame(game, "Player " + playerId + " joined");

        return game;
    }

    public Game removePlayerFromGame(Long gameId, Long playerId) throws GameNotFoundException, PlayerNotFoundException, CannotLeaveRunningGameException {
        Game game = getGameById(gameId);

        if (!game.getStatus().equals(GameStatus.OPEN)){
            throw new CannotLeaveRunningGameException("You can not leave a running game");
        }

        game.removePlayer(playerService.getPlayerById(playerId));
        gameRepository.save(game);
        messageService.sendMessageToGame(game, "Player " + playerId + " left");
        return game;
    }

    public Game setPlayerReady(Long gameId, Long playerId, Boolean ready) throws GameNotFoundException, PlayerNotFoundException, RuleSetViolationException {
        Game game = getGameById(gameId);
        playerService.setPlayerReady(playerId, ready);

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

        Card card = cardService.getCardById(cardId);
        Player origin = playerService.getPlayerById(playerId);
        Player target = playerService.getPlayerById(targetPlayerId);

        log.info("Player {} playing card {} targeting player {}", origin.getId(), card.getName(), target.getId());
        game.playCard(origin, target, card);
        gameRepository.save(game);

        if (game.isTurnOver()){
            startNextTurn(game);
        }
    }

    private Game startGame(Game game) throws RuleSetViolationException {
        while (game.getPlayers().size() < configuration.getMaximumNumberOfPlayers()){
            game.addPlayer(playerService.createPlayer(true));
        }
        messageService.sendMessageToGame(game, "And so the " + game.getPlayers().size() + " of you arrive in the city. Who can you trust? And who is a traitor?");
        game.start();
        startNextTurn(game);
        return gameRepository.save(game);
    }

    private void startNextTurn(Game game){
        messageService.sendMessageToGame(game, "A new day dawns..");
        game.startNextTurn();
        game.getAIPlayers().forEach(player -> {
                    Action recommendedAction = artificialIntelligenceService.getRecommendedAction(game, player.getId());
                    try {
                        playCard(
                                game.getId(),
                                game.getTurn(),
                                player.getHandCards().get(recommendedAction.getCardSlot()).getId(),
                                player.getId(),
                                game.getPlayers().get(recommendedAction.getPlayerSlot()).getId());
                    } catch (Exception e) {
                        log.error("AI failed to make valid move, skipping turn: {}", e.getMessage());
                    }
                }
        );
    }

    public Game getGameById(Long id) throws GameNotFoundException {
        Game game = gameRepository.findById(id).get();
        if (game == null){
            throw new GameNotFoundException("Game not found");
        }
        return game;
    }

    @Deprecated
    public Turn getTurnByGameIdAndCounter(Long gameId, Integer counter) throws TurnNotFoundException {
        Optional<Turn> turn = turnRepository.findByGameIdAndCounter(gameId, counter);
        if (!turn.isPresent()){
            throw new TurnNotFoundException("Turn not found");
        }
        return turn.get();
    }

    public Game getGameByPlayerId(Long playerId) throws PlayerNotInGameException, PlayerNotFoundException, GameNotFoundException {
        Player player = playerService.getPlayerById(playerId);
        if (player.getGameId() == null){
            throw new PlayerNotInGameException("Currently not playing a game");
        }

        return getGameById(player.getGameId());
    }
}
