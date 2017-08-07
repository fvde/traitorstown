package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.ai.learning.model.DiscreteActionSpace;
import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import com.individual.thinking.traitorstown.model.Resource;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public static final String AUTHENTICATION_KEY = "player";
    public static final Integer MINIMUM_AMOUNT_OF_PLAYERS = 2;
    public static final Integer MAXIMUM_AMOUNT_OF_PLAYERS = 8;
    public static final Integer MAXIMUM_AMOUNT_OF_CARDS = 8;
    public static final Integer INITIAL_AMOUNT_OF_CARDS = 3;
    public static final Integer INITIAL_AMOUNT_OF_GOLD = 10;
    public static final Integer INITIAL_AMOUNT_OF_REPUTATION = 10;
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
    public static final DiscreteActionSpace ACTION_SPACE = new DiscreteActionSpace(Configuration.MAXIMUM_AMOUNT_OF_CARDS, MAXIMUM_AMOUNT_OF_PLAYERS);
    public static final ObservationSpace<GameState> OBSERVATION_SPACE = new ArrayObservationSpace(new int[]{ARRAY_OBSERVATION_SPACE_SIZE});
}
