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
public class CandidacyEffect extends SpecialEffect {

    @Builder
    protected CandidacyEffect() {
        super(Visibility.ALL, Integer.MAX_VALUE);
    }

    @Override
    public void apply(Game game, Player origin, Player target) { }
}
