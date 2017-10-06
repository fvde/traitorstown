package com.individual.thinking.traitorstown.model;

import lombok.Value;

import java.util.Map;

@Value
public class Voting {
    Boolean endedWithTie;
    Boolean noVotesWereCast;
    Player winner;

    public Voting(Map<Player, Long> votes){
        boolean tie = false;
        Long highestVoting = 0L;
        Player electedPlayer = null;

        for (Player candidate : votes.keySet()){
            Long voting = votes.get(candidate);
            if (voting > highestVoting){
                highestVoting = voting;
                electedPlayer = candidate;
                tie = false;
            } else if (voting == highestVoting){
                tie = true;
            }
        }

        endedWithTie = tie;
        winner = !tie ? electedPlayer : null;
        noVotesWereCast = highestVoting == 0;
    }
}
