package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public class Configuration {
    public static final Integer TOTAL_NUMBER_OF_CARDS = 7;
    public static final Integer ARRAY_OBSERVATION_SPACE_SIZE = GameState.GAME_STATE_GENERAL_INFORMATION_SIZE + TOTAL_NUMBER_OF_CARDS;
    public static final ObservationSpace<GameState> OBSERVATION_SPACE = new ArrayObservationSpace(new int[]{ARRAY_OBSERVATION_SPACE_SIZE});
}
