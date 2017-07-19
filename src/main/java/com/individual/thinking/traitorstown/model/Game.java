package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.game.rules.RuleSet;
import com.individual.thinking.traitorstown.game.rules.RuleSetViolationException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    @JoinColumn(name = "game_id")
    private List<Player> players = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id")
    @OrderBy("counter DESC")
    private List<Turn> turns = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Setter
    private GameStatus status = GameStatus.OPEN;

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void start() throws RuleSetViolationException {
        setStatus(GameStatus.PLAYING);
        turns.add(Turn.builder().counter(1).build());
        RuleSet.getRolesForPlayers(players)
                .forEach((player, role) -> player.setRole(role));

        //TODO set deck
        //TODO draw cards
    }

    public Integer getReadyPlayers(){
        return players.stream().filter(Player::getReady).collect(Collectors.toList()).size();
    }

    @Tolerate
    public Game() {}
}
