package com.individual.thinking.traitorstown.ai.learning;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import com.individual.thinking.traitorstown.model.Role;
import com.individual.thinking.traitorstown.model.exceptions.RuleSetViolationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import static com.individual.thinking.traitorstown.Configuration.ARRAY_OBSERVATION_SPACE_SIZE;

@Slf4j
public class TraitorsTownMDP implements MDP<Game, Integer, DiscreteSpace> {

    private Game game;
    private Role AIRole;

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

    public TraitorsTownMDP(int handSize, int maxPlayers, boolean debug){
        assert (handSize > maxPlayers);
        this.handSize = handSize;
        this.maxPlayers = maxPlayers;
        this.debug = debug;
        this.actionSpace = new DiscreteSpace(handSize * maxPlayers);
        this.observationSpace = new ArrayObservationSpace(new int[]{ARRAY_OBSERVATION_SPACE_SIZE});

        this.game = reset();
    }

    @Override
    public Game reset() {
        Game game = Game.buildAITrainingGame();
        try {
            game.start();
            AIRole = game.getPlayers().get(0).getRole();
        } catch (RuleSetViolationException e) {
            e.printStackTrace();
        }

        assert (AIRole != null);
        return game;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<Game> step(Integer action) {
        printTest(action);
        game.playCardAsPlayerFromCardSlotTargetingPlayer(0, getTargetPlayerFromAction(action), getCardSlotFromAction(action));
        double reward = game.getStatus().equals(GameStatus.FINISHED) && game.getWinner().equals(AIRole) ?
                1.0 :
                0.0;
        return new StepReply<>(game, reward, isDone(), null);
    }

    @Override
    public boolean isDone() {
        return game.getStatus().equals(GameStatus.FINISHED);
    }

    @Override
    public TraitorsTownMDP newInstance() {
        return new TraitorsTownMDP(handSize, maxPlayers, debug);
    }

    private int getCardSlotFromAction(Integer action){
        return action / handSize;
    }

    private int getTargetPlayerFromAction(Integer action){
        return action - (getCardSlotFromAction(action) * handSize);
    }
}
