package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@ToString(callSuper = true)
@Getter
public class DrawCardEffect extends SpecialEffect {

    @Builder
    protected DrawCardEffect() {
        super(Visibility.PLAYER, 1);
    }

    @Override
    public void apply(Game game, Player player, Player target) {
        target.drawCard();
    }
}
