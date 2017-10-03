package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;
import java.util.Optional;

@Entity
@ToString(callSuper = true)
@Getter
public class MayorEffect extends SpecialEffect {

    @Builder
    protected MayorEffect(Integer duration) {
        super(Visibility.ALL, duration);
    }

    @Tolerate
    MayorEffect(){
        // hibernate
    }

    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {
        if (isNew){
            publishMessage(
                    Message.builder().content(" was elected mayor!").structure(MessageStructure.PREFIX_TARGET).build(),
                    game.getId(),
                    game.getPlayers(),
                    Optional.empty(),
                    Optional.of(target));
        }
    }
}
