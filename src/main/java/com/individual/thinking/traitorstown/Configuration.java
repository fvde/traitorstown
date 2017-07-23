package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.model.Resource;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public static final String AUTHENTICATION_KEY = "player";
    public static final Integer MINIMUM_AMOUNT_OF_PLAYERS = 1;
    public static final Integer MAXIMUM_AMOUNT_OF_PLAYERS = 8;
    public static final Integer INITIAL_AMOUNT_OF_CARDS = 3;
    public static final Integer INITIAL_AMOUNT_OF_GOLD = 10;
    public static final Integer INITIAL_AMOUNT_OF_REPUTATION = 0;
    public static final Map<Resource, Integer> MINIMUM_RESOURCES;
    static
    {
        MINIMUM_RESOURCES = new HashMap<>();
        MINIMUM_RESOURCES.put(Resource.GOLD, 0);

        // reputation may fall below zero
        MINIMUM_RESOURCES.put(Resource.REPUTATION, Integer.MIN_VALUE);

        // effect may cause you to discard cards, which is always allowed
        MINIMUM_RESOURCES.put(Resource.CARDS, Integer.MIN_VALUE);
    }
}
