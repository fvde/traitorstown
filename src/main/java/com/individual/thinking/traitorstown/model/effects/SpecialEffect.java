package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;

@Entity
@ToString(callSuper = true)
@Getter
public abstract class SpecialEffect extends Effect {

    protected SpecialEffect(Visibility visibility, Integer duration) {
        super(visibility == null ? Visibility.PLAYER : visibility,
                duration);
    }

    @Tolerate
    SpecialEffect(){
        // hibernate
    }


    @Override
    public boolean mayApply(Player target) {
        return true;
    }

    @Override
    public boolean isCost() {
        return false;
    }

    @Override
    public String getName() {
        return "ADD STATUS " + getClass().getSimpleName();
    }
}
