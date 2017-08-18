package com.individual.thinking.traitorstown.ai.learning.model;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.learning.RewardService;
import com.individual.thinking.traitorstown.game.GameService;
import com.individual.thinking.traitorstown.game.exceptions.PlayerNotFoundException;
import com.individual.thinking.traitorstown.model.Card;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
public class TraitorsTownMDP implements MDP<GameState, Integer, DiscreteSpace> {

    private GameService gameService;
    private RewardService rewardService;
    private final Long aiPlayerId;
    private List<Long> players;
    private int timesInSameTurn = 0;
    private GameState gameState;
    private Long gameId = 0L;
    private Integer turn = 0;
    private Random random;

    @Getter
    private final DiscreteActionSpace actionSpace;
    private final TraitorsTownConfiguration configuration;

    @Getter
    private final ObservationSpace<GameState> observationSpace;

    public TraitorsTownMDP(GameService gameService, RewardService rewardService, List<Long> players, DiscreteActionSpace actionSpace, TraitorsTownConfiguration configuration){
        this.gameService = gameService;
        this.rewardService = rewardService;
        this.aiPlayerId = players.get(0);
        this.players = players;
        this.actionSpace = actionSpace;
        this.configuration = configuration;
        this.observationSpace = Configuration.OBSERVATION_SPACE;
        this.random = new Random();

        reset();
    }

    @Override
    public GameState reset() {
        Game game = gameService.createNewGame();
        try {
            int desiredAmountOfOpponents = random.nextInt(configuration.getMaximumNumberOfPlayers());
            int opponentIndex = 1;
            while (desiredAmountOfOpponents > 0){
                game = gameService.addPlayerToGame(game.getId(), players.get(opponentIndex++));
                desiredAmountOfOpponents--;
            }

            // join game so enough 'human' players are ready
            game = gameService.addPlayerToGame(game.getId(), aiPlayerId);
        } catch (Exception e) {
            log.error("Failed to reset game with exception {}", e);
        }

        assert (game.getStatus() == GameStatus.PLAYING);

        timesInSameTurn = 0;
        gameState = GameState.fromGameAndPlayer(game, aiPlayerId);
        gameId = game.getId();
        turn = game.getTurn();

        return gameState;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<GameState> step(Integer a) {
        Action action = actionSpace.convert(a);
        int currentTurn = turn;
        double reward = 0.0;

        log.info("Game: " + gameId + " Turn: " + turn + ": " + getReadable(action) + " with state " + gameState.toString() + "("+ timesInSameTurn +")");

        try {
            gameService.playCard(
                    gameId,
                    turn,
                    getCardIdFromAction(action),
                    aiPlayerId,
                    players.get(action.getPlayerSlot()));

            Game game = gameService.getGameById(gameId);
            gameState = GameState.fromGameAndPlayer(game, aiPlayerId);
            turn = game.getTurn();
            reward = rewardService.getReward(gameState, turn);

        } catch (Exception e) {
            log.error("Failed to execute action {} with exception {}, turn {}, card {}, target player {} ",
                    action,
                    e.getMessage(),
                    turn,
                    getCardIdFromAction(action),
                    players.get(action.getPlayerSlot()));
        }

        if (isDone()){
            log.info("---------");
            log.info(gameState.isWinner() ? "VICTORY!" : "GAME OVER");
            log.info("---------");
        }

        if (turn == currentTurn){
            timesInSameTurn++;
        } else {
            timesInSameTurn = 0;
        }

        return new StepReply<>(gameState, reward, isDone(), null);
    }

    @Override
    public boolean isDone() {
        return gameState.getWinner() > 0 || timesInSameTurn > 50;
    }

    @Override
    public TraitorsTownMDP newInstance() {
        return new TraitorsTownMDP(gameService, rewardService, players, actionSpace, configuration);
    }

    private Long getCardIdFromAction(Action action) {
        List<Card> playerCards;
        try {
            playerCards = gameService.getPlayerCards(aiPlayerId);
        } catch (PlayerNotFoundException e) {
            playerCards = Collections.emptyList();
        }
        return action.getCardSlot() < playerCards.size() ? playerCards.get(action.getCardSlot()).getId() : -1;
    }

    public String getReadable(Action action){
        try {
            Long cardId = getCardIdFromAction(action);
            Optional<Card> card = gameService.getPlayerCards(aiPlayerId).stream().filter(c -> c.getId() == cardId).findFirst();
            if (card.isPresent()){
                return "Playing card " + card.get().getName() + " targeting player " + action.getPlayerSlot();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Undefined action";
    }
}
