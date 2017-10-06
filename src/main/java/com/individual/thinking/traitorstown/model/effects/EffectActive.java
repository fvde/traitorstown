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
    @JoinColumn(name = "origin_id")
    @NonNull
    private Player origin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "target_id")
    @NonNull
    private Player target;

    @Tolerate
    EffectActive() {}

    public void apply(Game game){
        effect.apply(game, origin, target, remainingTurns.equals(effect.getDuration()));
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

    public boolean isKillVote() {
        return effect.isOfType(VoteKillEffect.class);
    }

    public boolean isSpareVote() {
        return effect.isOfType(VoteSpareEffect.class);
    }

    public boolean isMayor() {
        return effect.isOfType(MayorEffect.class);
    }

    public boolean isTrial() {
        return effect.isOfType(TrialEffect.class);
    }

    public boolean isTraitor() {
        return effect.isOfType(TraitorEffect.class);
    }

    public boolean isCitizen() {
        return effect.isOfType(CitizenEffect.class);
    }

    public boolean isNotAtHome() {
        return effect.isOfType(NotAtHomeEffect.class);
    }

    public boolean isParty()  {
        return effect.isOfType(PartyEffect.class) && remainingTurns == 1;
    }

    public boolean isDeath()  {
        return effect.isOfType(DeathEffect.class);
    }

    public boolean isPartyWithGuest(Player origin)  {
        return effect.isOfType(AttendPartyEffect.class) && origin.is(origin);
    }

    public boolean isTradeFromPlayer(Player player)  {
        return effect.isOfType(TradeEffect.class) && origin.is(player);
    }

    public boolean isVisibleFor(Player player){
        if (player == null){
            return false;
        }

        switch (effect.getVisibility()) {
            case NONE: return false;
            case ALL: return true;
            case FACTION: return target.getRole().equals(player.getRole());
            case PLAYER: return target.getId().equals(player.getId());
            case PLAYER_AND_ORIGIN: return target.getId().equals(player.getId()) || origin.getId().equals(player.getId());
        }

        return false;
    }
}
