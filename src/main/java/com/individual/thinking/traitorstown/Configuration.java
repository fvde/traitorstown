package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import com.individual.thinking.traitorstown.model.ResourceType;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public static final Map<ResourceType, Integer> MINIMUM_RESOURCES;
    static
    {
        // default minimum is zero
        MINIMUM_RESOURCES = new HashMap<>();
        for (ResourceType type : ResourceType.values()){
            MINIMUM_RESOURCES.put(type, 0);
        }

        // reputation may fall below zero
        MINIMUM_RESOURCES.put(ResourceType.REPUTATION, Integer.MIN_VALUE);
    }

    public static final Integer TOTAL_NUMBER_OF_CARDS = 3;
    public static final Integer ARRAY_OBSERVATION_SPACE_SIZE = GameState.GAME_STATE_GENERAL_INFORMATION_SIZE + TOTAL_NUMBER_OF_CARDS;
    public static final ObservationSpace<GameState> OBSERVATION_SPACE = new ArrayObservationSpace(new int[]{ARRAY_OBSERVATION_SPACE_SIZE});
}
