package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.model.exceptions.AlreadyPlayedCardThisTurnException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "turn_id")
    @NonNull
    private List<CardPlayed> cardsPlayed = new ArrayList<>();

    @Tolerate
    Turn () {}

    public void playCard(Card card, Player player, Player target) throws AlreadyPlayedCardThisTurnException {
        if (cardsPlayed.stream().anyMatch(c -> c.getPlayer().equals(player.getId()))){
            throw new AlreadyPlayedCardThisTurnException("Already played a card this turn");
        }
        cardsPlayed.add(CardPlayed.builder()
                .card(card)
                .player(player)
                .target(target)
                .build());
    }

    public Turn startNext(){
        // TODO: apply all played cards
        return Turn.builder()
                .counter(counter + 1)
                .cardsPlayed(emptyList())
                .build();
    }
}
