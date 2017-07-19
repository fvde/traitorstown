package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Turn;
import lombok.Data;

@Data
public class TurnRepresentation {
    private final Integer counter;

    public static TurnRepresentation fromTurn(Turn turn){
        return new TurnRepresentation(turn.getCounter());
    }
}
