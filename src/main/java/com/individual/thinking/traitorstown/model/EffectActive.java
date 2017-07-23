package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@Getter
public class EffectActive {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "effect_id")
    @NonNull
    private Effect effect;

    @NonNull
    private Integer remainingTurns;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    @NonNull
    private Player player;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "target_id")
    @NonNull
    private Player target;

    @Tolerate
    EffectActive() {}

    public void apply(){
        effect.apply(player, target);
        remainingTurns--;
    }

    public boolean isActive(){
        return remainingTurns > 0;
    }
}
