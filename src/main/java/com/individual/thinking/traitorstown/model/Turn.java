package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.model.exceptions.AlreadyPlayedCardThisTurnException;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"game_id", "counter"})
})
public class Turn {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "game_id")
    private Long gameId;

    @NonNull
    private Integer counter;

    @ManyToMany
    @JoinTable(name = "turn_player", joinColumns = @JoinColumn(name = "turn_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"))
    private List<Player> finishedPlayers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "turn_id")
    private List<EffectActive> activeEffects = new ArrayList<>();

    @Tolerate
    Turn () {}

    public void playCard(Card card, Player player, Player target) throws AlreadyPlayedCardThisTurnException, PlayerDoesNotHaveCardException {
        if (finishedPlayers.contains(player)){
            throw new AlreadyPlayedCardThisTurnException("Already played a card this turn");
        }
        player.playCard(card);
        activeEffects.addAll(
                card.getEffects().stream().map( effect ->
                    EffectActive.builder()
                            .effect(effect)
                            .player(player)
                            .target(target)
                            .remainingTurns(effect.getDuration())
                            .build())
                    .collect(Collectors.toList()));

        finishedPlayers.add(player);
    }

    public Turn startNext(){
        activeEffects.stream().forEach(EffectActive::apply);
        return Turn.builder()
                .counter(counter + 1)
                .activeEffects(activeEffects.stream().filter(EffectActive::isActive).collect(Collectors.toList()))
                .build();
    }
}
