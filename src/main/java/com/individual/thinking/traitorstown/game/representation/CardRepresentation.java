package com.individual.thinking.traitorstown.game.representation;

import com.individual.thinking.traitorstown.model.Card;
import com.individual.thinking.traitorstown.model.Effect;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CardRepresentation {
    private final Long id;
    private final String name;
    private final String description;
    private final List<ResourceRepresentation> costs;

    public static CardRepresentation fromCard(Card card){
        return new CardRepresentation(card.getId(),
                card.getName(),
                card.getDescription(),
                card.getEffects().stream().filter(Effect::isCost).map(ResourceRepresentation::fromEffect).collect(Collectors.toList()));
    }
}
