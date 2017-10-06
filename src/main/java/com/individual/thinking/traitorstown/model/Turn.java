package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.TraitorstownApplication;
import com.individual.thinking.traitorstown.game.rules.Rules;
import com.individual.thinking.traitorstown.model.events.TurnEndedEvent;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import com.individual.thinking.traitorstown.model.exceptions.RuleSetViolationException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@ToString
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

    @NonNull
    private Integer humanPlayers;

    @Builder.Default
    private Integer inactivePlayers = 0;

    @NonNull
    private Date startedAt;

    @ManyToMany
    @JoinTable(name = "turn_player", joinColumns = @JoinColumn(name = "turn_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"))
    private List<Player> finishedPlayers = new ArrayList<>();

    @Tolerate
    Turn () {}

    public void playCard(Card card, Player player, Player target) throws PlayerDoesNotHaveCardException, RuleSetViolationException {

        if (finishedPlayers.contains(player)){
            throw new RuleSetViolationException("Already played a card this turn");
        }

        player.playCard(card, target);

        if (!player.isAi()){
            finishedPlayers.add(player);
        }
    }

    public void end(){
        if (finishedPlayers.size() == humanPlayers || startedAt.toInstant().plusSeconds(Rules.TURN_DURATION_IN_SECONDS).isAfter(Instant.now())){
            inactivePlayers = humanPlayers - finishedPlayers.size();
            TraitorstownApplication.EventBus.post(TurnEndedEvent.builder().turn(this).build());
        }
    }

    public boolean allHumanPlayersInactive(){
        return inactivePlayers.equals(humanPlayers);
    }

    public Turn startNext(){
        return Turn.builder()
                .counter(counter + 1)
                .finishedPlayers(finishedPlayers.stream().filter(Player::isDead).collect(Collectors.toList()))
                .humanPlayers(humanPlayers)
                .inactivePlayers(0)
                .startedAt(new Date())
                .build();
    }
}
