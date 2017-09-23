package com.individual.thinking.traitorstown.game.rules;

import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Role;
import com.individual.thinking.traitorstown.model.exceptions.RuleSetViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Rules {

    public static final Integer INITIAL_AMOUNT_OF_CARDS = 2;
    public static final Integer INITIAL_AMOUNT_OF_GOLD = 10;
    public static final Integer INITIAL_AMOUNT_OF_REPUTATION = 10;
    public static Integer MAXIMUM_NUMBER_OF_CARDS = 8;

    @Autowired
    public Rules(TraitorsTownConfiguration configuration){
        MAXIMUM_NUMBER_OF_CARDS = configuration.getMaximumNumberOfCards();
    }

    public static Map<Role, List<Player>> getRolesForPlayers(List<Player> players) throws RuleSetViolationException {
        Collections.shuffle(players);
        Map<Role, List<Player>> roles = new HashMap<>();
        switch(players.size()){
            case 1 : {
                roles.put(Role.CITIZEN, Arrays.asList(players.get(0)));
                break;
            }
            case 2 : {
                roles.put(Role.CITIZEN, Arrays.asList(players.get(0)));
                roles.put(Role.TRAITOR, Arrays.asList(players.get(1)));
                break;
            }
            case 3 : {
                roles.put(Role.CITIZEN, Arrays.asList(players.get(0), players.get(1)));
                roles.put(Role.TRAITOR, Arrays.asList(players.get(2)));
                break;
            }
            case 4 : {
                roles.put(Role.CITIZEN, Arrays.asList(players.get(0), players.get(1), players.get(2)));
                roles.put(Role.TRAITOR, Arrays.asList(players.get(3)));
                break;
            }
            case 5 : {
                roles.put(Role.CITIZEN, Arrays.asList(players.get(0), players.get(1), players.get(2)));
                roles.put(Role.TRAITOR, Arrays.asList(players.get(3), players.get(4)));
                break;
            }
            case 6 : {
                roles.put(Role.CITIZEN, Arrays.asList(players.get(0), players.get(1), players.get(2), players.get(3)));
                roles.put(Role.TRAITOR, Arrays.asList(players.get(4), players.get(5)));
                break;
            }
            case 7 : {
                roles.put(Role.CITIZEN, Arrays.asList(players.get(0), players.get(1), players.get(2), players.get(3), players.get(4)));
                roles.put(Role.TRAITOR, Arrays.asList(players.get(5), players.get(6)));
                break;
            }
            case 8 : {
                roles.put(Role.CITIZEN, Arrays.asList(players.get(0), players.get(1), players.get(2), players.get(3), players.get(4)));
                roles.put(Role.TRAITOR, Arrays.asList(players.get(5), players.get(6), players.get(7)));
                break;
            }
            default: {
                throw new RuleSetViolationException("Unsupported number of players");
            }
        }

        return roles;
    }
}