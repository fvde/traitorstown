package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.TraitorstownApplication;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Visibility;
import com.individual.thinking.traitorstown.model.events.MessageEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Tolerate;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@ToString
@Slf4j
public abstract class Effect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @NonNull
    private EffectTargetType effectTargetType;

    @NonNull
    private Integer duration;

    Effect(Visibility visibility, EffectTargetType effectTargetType, Integer duration){
        this.visibility = visibility;
        this.effectTargetType = effectTargetType;
        this.duration = duration;
    }

    @Tolerate
    Effect(){
        // for hibernate
    }

    public abstract void apply(Game game, Player origin, Player target, boolean isNew);
    public abstract boolean mayApply(Player origin, Player target);
    public abstract boolean isCost();
    public abstract String getName();
    public boolean isOfType(Class<? extends Effect> type) {
        return getClass() == type;
    }

    public EffectActive toActive(Player origin, Player target){
        return EffectActive.builder()
                .effect(this)
                .origin(origin)
                .target(target)
                .remainingTurns(duration)
                .build();
    }

    public void publishMessage(String content, List<Player> recipients){
        if (recipients.isEmpty()){
            log.warn("Trying to send message without recipients...");
            return;
        }

        TraitorstownApplication.EventBus.post(
                MessageEvent.builder()
                        .content(content)
                        .gameId(recipients.get(0).getGameId())
                        .recipients(recipients.stream().map(player -> player.getId()).collect(Collectors.toList()))
                        .from(-1L)
                        .build());
    }
}
