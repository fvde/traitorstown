package com.individual.thinking.traitorstown.ai.learning;

import com.individual.thinking.traitorstown.game.GameService;
import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import com.individual.thinking.traitorstown.model.Player;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.List;

import static com.individual.thinking.traitorstown.Configuration.ARRAY_OBSERVATION_SPACE_SIZE;

@Slf4j
public class TraitorsTownMDP implements MDP<Game, Integer, DiscreteSpace> {

    private Long gameId;
    private GameService gameService;
    private Long aiPlayerId;
    private List<Long> players;
    private final int handSize;
    private final int maxPlayers;
    private final boolean debug;

    @Getter
    private final DiscreteSpace actionSpace;

    @Getter
    private final ObservationSpace<Game> observationSpace;


    public void printTest(Integer action) {
        if (debug){
            log.info("Executing action {}", action);
        }
    }

    public TraitorsTownMDP(GameService gameService, List<Long> players, int handSize, boolean debug){
        this.gameService = gameService;
        this.aiPlayerId = players.get(0);
        this.players = players;
        this.handSize = handSize;
        this.maxPlayers = players.size();
        assert (handSize > maxPlayers);
        this.debug = debug;
        this.actionSpace = new DiscreteSpace(handSize * maxPlayers);
        this.observationSpace = new ArrayObservationSpace(new int[]{ARRAY_OBSERVATION_SPACE_SIZE});

        reset();
    }

    @Override
    public Game reset() {
        Game game = gameService.createNewGame();
        gameId = game.getId();
        try {
            game = gameService.addPlayerToGame(game.getId(), aiPlayerId);
            while (game.getStatus() != GameStatus.PLAYING){
                int opponentIndex = 1;
                game = gameService.addPlayerToGame(game.getId(), players.get(opponentIndex++));
            }
        } catch (Exception e) {
            log.error("Failed to reset game with exception {}", e);
        }

        assert (game.getStatus() == GameStatus.PLAYING);
        return game;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<Game> step(Integer action) {
        printTest(action);
        Game game = null;
        try {
            game = gameService.getGameById(gameId);
        } catch (GameNotFoundException e) {
            e.printStackTrace();
        }
        double reward = 0.0;
        try {
            Player aiPlayer = gameService.getPlayerById(aiPlayerId);
            gameService.playCard(
                    gameId,
                    game.getCurrentTurn().get().getCounter(),
                    getCardIdFromAction(action, aiPlayer),
                    aiPlayerId,
                    players.get(getTargetPlayerSlotFromAction(action)));

            // check results
            game = gameService.getGameById(gameId);

            // reward victory
            reward += game.getStatus().equals(GameStatus.FINISHED) && game.getWinner().equals(gameService.getPlayerById(aiPlayerId).getRole()) ?
                    1.0 :
                    0.0;

            // reward playing correct cards
            // reward += game.getCurrentTurn().get().getCounter();
        } catch (Exception e) {
            // log.error("Failed to execute step with exception {}", e.getMessage());
            reward = -1.0;
        }
        log.info(game.toString());

        return new StepReply<>(game, reward, isDone(), null);
    }

    @Override
    public boolean isDone() {
        try {
            return gameService.getGameById(gameId).getStatus().equals(GameStatus.FINISHED);
        } catch (GameNotFoundException e) {
            log.error("Failed to check if game is over with exception {}", e);
        }

        return false;
    }

    @Override
    public TraitorsTownMDP newInstance() {
        return new TraitorsTownMDP(gameService, players, handSize, debug);
    }

    private Long getCardIdFromAction(Integer action, Player player){
        int slot = getCardSlotFromAction(action);
        return slot < player.getHandCards().size() ? player.getHandCards().get(slot).getId() : Long.MAX_VALUE;
    }

    private int getCardSlotFromAction(Integer action){
        return action % handSize;
    }

    private int getTargetPlayerSlotFromAction(Integer action){
        return action / handSize;
    }
}
