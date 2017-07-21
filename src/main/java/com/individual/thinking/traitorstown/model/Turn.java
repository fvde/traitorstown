package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.model.exceptions.AlreadyPlayedCardThisTurnException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @JoinTable(name = "turn_card", joinColumns = @JoinColumn(name = "turn_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"))
    private List<Card> cards = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "turn_player", joinColumns = @JoinColumn(name = "turn_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"))
    private List<Player> finishedPlayers = new ArrayList<>();

    @Tolerate
    Turn () {}

    public void playCard(Card card, Player player) throws AlreadyPlayedCardThisTurnException {
        if (finishedPlayers.contains(player)){
            throw new AlreadyPlayedCardThisTurnException("Already played a card this turn");
        }
        cards.add(card);
        finishedPlayers.add(player);
    }

    public Turn startNext(){
        // TODO: apply all played cards
        return Turn.builder().counter(counter + 1).build();
    }
}
