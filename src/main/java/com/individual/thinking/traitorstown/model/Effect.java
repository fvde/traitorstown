package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.Configuration;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@Getter
public class Effect {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @NonNull
    private EffectType type;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Resource targetType;

    @NonNull
    private Integer amount;

    @NonNull
    private Integer duration;

    @Tolerate
    Effect() {}

    public void apply(Player player, Player target) {
        target.setResource(targetType, type.apply(target.getResource(targetType), amount));
    }

    public boolean mayApply(Player player){
        if (type.equals(EffectType.REMOVE)){
            return player.getResource(targetType) - amount >= Configuration.MINIMUM_RESOURCES.get(targetType);
        }

        return true;
    }

    public boolean isCost(){
        return type.equals(EffectType.REMOVE);
    }

    @Override
    public String toString() {
        return "Effect{" +
                "type=" + type +
                ", targetType=" + targetType +
                ", amount=" + amount +
                '}';
    }
}
