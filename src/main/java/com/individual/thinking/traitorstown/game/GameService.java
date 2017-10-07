package com.individual.thinking.traitorstown.game;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.TraitorstownApplication;
import com.individual.thinking.traitorstown.ai.ArtificialIntelligenceService;
import com.individual.thinking.traitorstown.ai.learning.model.Action;
import com.individual.thinking.traitorstown.game.exceptions.*;
import com.individual.thinking.traitorstown.game.repository.GameRepository;
import com.individual.thinking.traitorstown.game.repository.TurnRepository;
import com.individual.thinking.traitorstown.message.MessageService;
import com.individual.thinking.traitorstown.model.*;
import com.individual.thinking.traitorstown.model.events.TurnEndedEvent;
import com.individual.thinking.traitorstown.model.exceptions.InactiveGameException;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import com.individual.thinking.traitorstown.model.exceptions.RuleSetViolationException;
import com.individual.thinking.traitorstown.model.exceptions.TargetPlayerNotInGameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final TurnRepository turnRepository;
    private final CardService cardService;
    private final MessageService messageService;
    private final TraitorsTownConfiguration configuration;
    private final PlayerService playerService;

    @Autowired(required = false)
    private ArtificialIntelligenceService artificialIntelligenceService;

    @Autowired
    GameService(GameRepository gameRepository, TurnRepository turnRepository, CardService cardService, MessageService messageService, TraitorsTownConfiguration configuration, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.turnRepository = turnRepository;
        this.cardService = cardService;
        this.messageService = messageService;
        this.configuration = configuration;
        this.playerService = playerService;
        TraitorstownApplication.EventBus.register(this);
    }


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

    public void playCard(Long gameId, Integer turn, Long cardId, Long playerId, Long targetPlayerId) throws GameNotFoundException, CardNotFoundException, PlayerNotFoundException, PlayerDoesNotHaveCardException, InactiveGameException, TargetPlayerNotInGameException, RuleSetViolationException {
        Game game = getGameById(gameId);

        if (!game.isCurrentTurn(turn)){
            throw new RuleSetViolationException("It is currently not turn " + turn);
        }

        Card card = cardService.getCardById(cardId);
        Player origin = playerService.getPlayerById(playerId);
        Player target = playerService.getPlayerById(targetPlayerId);

        log.info("Player {} playing card {} targeting player {}", origin.getId(), card.getName(), target.getId());
        game.playCard(origin, target, card);
        gameRepository.save(game);
    }

    private Game startGame(Game game) throws RuleSetViolationException {
        while (game.getPlayers().size() < configuration.getMaximumNumberOfPlayers()){
            game.addPlayer(playerService.createPlayer(true));
        }
        messageService.sendMessageToGame(game, "And so the " + game.getPlayers().size() + " of you arrive in the city. Who can you trust? And who is a traitor?");
        game.start();
        return gameRepository.save(game);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleEndOfTurn(TurnEndedEvent turnEndedEvent) throws GameNotFoundException {
        log.info("Turn is ending {}", turnEndedEvent.getTurn());
        startNextTurn(getGameById(turnEndedEvent.getTurn().getGameId()));
    }

    private void startNextTurn(Game game) {
        // let AI play
        makeAISuggestions(game);

        // begin next turn
        messageService.sendMessageToGame(game, "A new day dawns..");
        game.startNextTurn();
        gameRepository.save(game);
    }

    private void makeAISuggestions(Game game) {
        if (artificialIntelligenceService == null){
            return;
        }

        game.getAIPlayers()
                .stream()
                .filter(Player::isAlive)
                .forEach(player -> {
                    Action recommendedAction = artificialIntelligenceService.getRecommendedAction(game, player.getId());
                    try {
                        playCard(
                                game.getId(),
                                game.getCurrentTurnCounter(),
                                player.getHandCards().get(recommendedAction.getCardSlot()).getId(),
                                player.getId(),
                                game.getPlayers().get(recommendedAction.getPlayerSlot()).getId());
                    } catch (GameNotFoundException e) {
                        log.info("AI failed to make move with exception {}", e.getMessage());
                    } catch (CardNotFoundException e) {
                        log.info("AI failed to make move with exception {}", e.getMessage());
                    } catch (PlayerNotFoundException e) {
                        log.info("AI failed to make move with exception {}", e.getMessage());
                    } catch (PlayerDoesNotHaveCardException e) {
                        log.info("AI failed to make move with exception {}", e.getMessage());
                    } catch (InactiveGameException e) {
                        log.info("AI failed to make move with exception {}", e.getMessage());
                    } catch (TargetPlayerNotInGameException e) {
                        log.info("AI failed to make move with exception {}", e.getMessage());
                    } catch (RuleSetViolationException e) {
                        log.info("AI failed to make move with exception {}", e.getMessage());
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
