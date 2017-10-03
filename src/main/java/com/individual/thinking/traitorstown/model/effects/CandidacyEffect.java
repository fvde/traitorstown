package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.Optional;

@Entity
@ToString(callSuper = true)
@Getter
public class CandidacyEffect extends SpecialEffect {

    @Builder
    protected CandidacyEffect() {
        super(Visibility.ALL, Integer.MAX_VALUE);
    }

    @Override
    public void apply(Game game, Player origin, Player target, boolean isNew) {
        if (isNew){
            publishMessage(
                    Message.builder().content(" just applied for the mayor position").structure(MessageStructure.PREFIX_TARGET).build(),
                    game.getId(),
                    game.getPlayers(),
                    Optional.empty(),
                    Optional.of(target));
        }
    }
}
