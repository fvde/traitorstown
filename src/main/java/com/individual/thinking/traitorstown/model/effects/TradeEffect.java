package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.ResourceType;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;
import java.util.Collections;

@Entity
@ToString(callSuper = true)
@Getter
public class TradeEffect extends SpecialEffect {

    public static int TRADE_REWARD = 3;

    @Builder
    protected TradeEffect(Integer duration) {
        super(Visibility.PLAYER, duration);
    }

    @Tolerate
    TradeEffect(){ }

    /***
     * Receive gold if another player trades with you
     */
    @Override
    public void apply(Game game, Player origin, Player target, boolean isNew) {
        if (isNew){
            if (origin.isTradingWith(target)){
                publishMessage(origin.getName() + " has accepted your offer to trade!", Collections.singletonList(target));
            } else {
                publishMessage(origin.getName() + " would like to trade with you!", Collections.singletonList(target));
            }
        }

        if (target.isTradingWith(origin) && origin.isTradingWith(target)){
            origin.addResource(ResourceType.GOLD, TRADE_REWARD);
        }
    }

    @Override
    public boolean mayApply(Player origin, Player target) {
        return target.isNot(origin) && !target.isTradingWith(origin);
    }
}
