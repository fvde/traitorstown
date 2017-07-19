package com.individual.thinking.traitorstown.game.rules;

import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Role;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleSet {
    public static Map<Player, Role> getRolesForPlayers(List<Player> players) throws RuleSetViolationException {
        Collections.shuffle(players);
        Map<Player, Role> roles = new HashMap<>();
        switch(players.size()){
            case 2 : {
                roles.put(players.get(0), Role.CITIZEN);
                roles.put(players.get(1), Role.TRAITOR);
                break;
            }
            case 3 : {
                roles.put(players.get(0), Role.CITIZEN);
                roles.put(players.get(1), Role.CITIZEN);
                roles.put(players.get(2), Role.TRAITOR);
                break;
            }
            case 4 : {
                roles.put(players.get(0), Role.CITIZEN);
                roles.put(players.get(1), Role.CITIZEN);
                roles.put(players.get(2), Role.CITIZEN);
                roles.put(players.get(3), Role.TRAITOR);
                break;
            }
            case 5 : {
                roles.put(players.get(0), Role.CITIZEN);
                roles.put(players.get(1), Role.CITIZEN);
                roles.put(players.get(2), Role.CITIZEN);
                roles.put(players.get(3), Role.TRAITOR);
                roles.put(players.get(4), Role.TRAITOR);
                break;
            }
            case 6 : {
                roles.put(players.get(0), Role.CITIZEN);
                roles.put(players.get(1), Role.CITIZEN);
                roles.put(players.get(2), Role.CITIZEN);
                roles.put(players.get(3), Role.CITIZEN);
                roles.put(players.get(4), Role.TRAITOR);
                roles.put(players.get(5), Role.TRAITOR);
                break;
            }
            case 7 : {
                roles.put(players.get(0), Role.CITIZEN);
                roles.put(players.get(1), Role.CITIZEN);
                roles.put(players.get(2), Role.CITIZEN);
                roles.put(players.get(3), Role.CITIZEN);
                roles.put(players.get(4), Role.CITIZEN);
                roles.put(players.get(5), Role.TRAITOR);
                roles.put(players.get(6), Role.TRAITOR);
                break;
            }
            case 8 : {
                roles.put(players.get(0), Role.CITIZEN);
                roles.put(players.get(1), Role.CITIZEN);
                roles.put(players.get(2), Role.CITIZEN);
                roles.put(players.get(3), Role.CITIZEN);
                roles.put(players.get(4), Role.CITIZEN);
                roles.put(players.get(5), Role.TRAITOR);
                roles.put(players.get(6), Role.TRAITOR);
                roles.put(players.get(7), Role.TRAITOR);
                break;
            }
            default: {
                throw new RuleSetViolationException("Unsupported number of players");
            }
        }

        return roles;
    }
}
