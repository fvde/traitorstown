package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public void apply(Game game){
        effect.apply(game, player, target, remainingTurns.equals(effect.getDuration()));
        remainingTurns--;
    }

    public boolean isActive(){
        return remainingTurns > 0;
    }

    public boolean isCandidacy() {
        return effect.isOfType(CandidacyEffect.class);
    }

    public boolean isVote() {
        return effect.isOfType(VoteEffect.class);
    }

    public boolean isMayor() {
        return effect.isOfType(MayorEffect.class);
    }

    public boolean isTraitor() {
        return effect.isOfType(TraitorEffect.class);
    }

    public boolean isCitizen() {
        return effect.isOfType(CitizenEffect.class);
    }

    public boolean isVisibleFor(Player player){
        if (player == null){
            return false;
        }

        switch (effect.getVisibility()) {
            case ALL: { return true; }
            case FACTION: return target.getRole().equals(player.getRole());
            case PLAYER: return target.getId().equals(player.getId());
        }

        return false;
    }
}
