package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.ArtificialIntelligenceService;
import com.individual.thinking.traitorstown.ai.learning.model.Action;
import com.individual.thinking.traitorstown.game.exceptions.*;
import com.individual.thinking.traitorstown.game.repository.GameRepository;
import com.individual.thinking.traitorstown.game.repository.PlayerRepository;
import com.individual.thinking.traitorstown.game.repository.TurnRepository;
import com.individual.thinking.traitorstown.message.MessageService;
import com.individual.thinking.traitorstown.model.*;
import com.individual.thinking.traitorstown.model.exceptions.*;
import com.individual.thinking.traitorstown.util.SwitchConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final TurnRepository turnRepository;
    private final CardService cardService;
    private final ArtificialIntelligenceService artificialIntelligenceService;
    private final MessageService messageService;
    private final TraitorsTownConfiguration configuration;

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
        messageService.sendMessageToGame(game, "Player " + playerId + " joined");

        return game;
    }

    public Game removePlayerFromGame(Long gameId, Long playerId) throws GameNotFoundException, PlayerNotFoundException, CannotLeaveRunningGameException {
        Game game = getGameById(gameId);

        if (!game.getStatus().equals(GameStatus.OPEN)){
            throw new CannotLeaveRunningGameException("You can not leave a running game");
        }

        game.removePlayer(getPlayerById(playerId));
        gameRepository.save(game);
        messageService.sendMessageToGame(game, "Player " + playerId + " left");
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

        Card card = cardService.getCardById(cardId);
        Player origin = getPlayerById(playerId);
        Player target = getPlayerById(targetPlayerId);

        log.info("Player {} playing card {} targeting player {}", origin.getId(), card.getName(), target.getId());
        game.playCard(origin, target, card);
        gameRepository.save(game);

        card.getMessages().stream().forEach(SwitchConsumer.<Message>
                inCase(m -> m.isForAll(), m -> messageService.sendMessageToGame(game, m.buildContent(origin, target)))
                .elseIf(m -> m.isForOrigin(), m -> messageService.sendMessageToPlayer(game, m.buildContent(origin, target), origin))
                .elseIf(m -> m.isForTarget(), m -> messageService.sendMessageToPlayer(game, m.buildContent(origin, target), target))
                .elseDefault(m -> log.error("Message {} had no recipient!", m))
        );

        if (game.isTurnOver()){
            startNextTurn(game);
        }
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

    private Game startGame(Game game) throws RuleSetViolationException {
        messageService.sendMessageToGame(game, "And so the " + game.getPlayers().size() + " of you arrive in the city. Who can you trust? Who to believe? And who is a traitor?");
        // TODO add AI players
        game.start();
        return gameRepository.save(game);
    }

    public List<Card> getPlayerCards(Long playerId) throws PlayerNotFoundException {
        return getPlayerCards(getPlayerById(playerId));
    }

    private List<Card> getPlayerCards(Player player){
        return player.getHandCards()
                .stream()
                .sorted(Comparator.comparing(Card::getId))
                .collect(Collectors.toList());
    }

    public Player getPlayerById(Long id) throws PlayerNotFoundException {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));
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
        Player player = getPlayerById(playerId);
        if (player.getGameId() == null){
            throw new PlayerNotInGameException("Currently not playing a game");
        }
        return getGameById(player.getGameId());
    }
}
