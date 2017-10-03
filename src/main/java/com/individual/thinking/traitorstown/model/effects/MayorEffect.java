package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;

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
    public void apply(Player player, Player target) {
        // does nothing currently, could add reputation or other?
    }
}
