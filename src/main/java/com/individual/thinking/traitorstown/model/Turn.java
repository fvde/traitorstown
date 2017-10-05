package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.model.exceptions.PlayedAlreadyPlayedCardThisTurnException;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import com.individual.thinking.traitorstown.model.exceptions.PlayerMayNotPlayThisCardException;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id")
    private Long gameId;

    @NonNull
    private Integer counter;

    @ManyToMany
    @JoinTable(name = "turn_player", joinColumns = @JoinColumn(name = "turn_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"))
    private List<Player> finishedPlayers = new ArrayList<>();

    @Tolerate
    Turn () {}

    public void playCard(Card card, Player player, Player target) throws PlayedAlreadyPlayedCardThisTurnException, PlayerDoesNotHaveCardException, PlayerMayNotPlayThisCardException {

        if (finishedPlayers.contains(player)){
            throw new PlayedAlreadyPlayedCardThisTurnException("Already played a card this turn");
        }

        if (!player.mayPlayCard(card, target)){
            throw new PlayerMayNotPlayThisCardException("The requirements to play this card are not met");
        }

        player.playCard(card, target);
        finishedPlayers.add(player);
    }

    public Turn end(){
        return Turn.builder()
                .counter(counter + 1)
                .build();
    }
}
