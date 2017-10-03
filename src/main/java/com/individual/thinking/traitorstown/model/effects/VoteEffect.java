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
public class VoteEffect extends SpecialEffect {

    @Builder
    protected VoteEffect(Integer duration) {
        super(Visibility.ALL, duration);
    }

    @Tolerate
    VoteEffect(){
        // hibernate
    }

    @Override
    public void apply(Game game, Player player, Player target, boolean isNew) {
        if (isNew){
            publishMessage(
                    Message.builder().content(" voted for ").structure(MessageStructure.PREFIX_ORIGIN_POSTFIX_TARGET).build(),
                    game.getId(),
                    game.getPlayers(),
                    Optional.of(player),
                    Optional.of(target));
        }
    }
}
