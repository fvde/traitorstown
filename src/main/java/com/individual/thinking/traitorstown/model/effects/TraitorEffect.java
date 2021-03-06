package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.ResourceType;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@ToString(callSuper = true)
@Getter
public class TraitorEffect extends SpecialEffect {

    @Builder
    protected TraitorEffect() {
        super(Visibility.FACTION, Integer.MAX_VALUE);
    }

    @Override
    public void apply(Game game, Player origin, Player target, boolean isNew) {
        origin.addResource(ResourceType.STOLEN_GOLD, 1);
    }
}
