package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Card;
import lombok.Data;

@Data
public class CardRepresentation {
    private final Long id;
    private final String name;

    public static CardRepresentation fromcard(Card card){
        return new CardRepresentation(card.getId(),
                card.getName());
    }
}
