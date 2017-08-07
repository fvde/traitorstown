package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@Getter
@ToString
public class EffectActive {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
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

    @Override
    public String toString() {
        return "EffectActive{" +
                "id=" + id +
                ", effect=" + effect +
                ", remainingTurns=" + remainingTurns +
                ", player=" + player.getId() +
                ", target=" + target.getId() +
                '}';
    }

    public boolean isCandidacy() {
        return effect.getTargetResource().equals(Resource.CANDIDACY);
    }

    public boolean isVote() {
        return effect.getTargetResource().equals(Resource.VOTE);
    }

    public boolean isMayor() {
        return effect.getTargetResource().equals(Resource.MAYOR);
    }
}
