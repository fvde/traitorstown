package com.individual.thinking.traitorstown.model;

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
    private EffectTargetType targetType;

    @NonNull
    private Integer amount;

    @NonNull
    private Integer duration;

    @Tolerate
    Effect() {}

    public void apply(Player origin, Player target){
        // TODO
    }
}
