package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import com.individual.thinking.traitorstown.model.Resource;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public static final Map<Resource, Integer> MINIMUM_RESOURCES;
    static
    {
        MINIMUM_RESOURCES = new HashMap<>();
        MINIMUM_RESOURCES.put(Resource.GOLD, 0);

        // reputation may fall below zero
        MINIMUM_RESOURCES.put(Resource.REPUTATION, Integer.MIN_VALUE);

        // effect may cause you to discard cards, which is always allowed
        MINIMUM_RESOURCES.put(Resource.CARD, Integer.MIN_VALUE);
    }

    public static final Integer TOTAL_NUMBER_OF_CARDS = 8;
    public static final Integer ARRAY_OBSERVATION_SPACE_SIZE = GameState.GAME_STATE_GENERAL_INFORMATION_SIZE + TOTAL_NUMBER_OF_CARDS;
    public static final ObservationSpace<GameState> OBSERVATION_SPACE = new ArrayObservationSpace(new int[]{ARRAY_OBSERVATION_SPACE_SIZE});
}
