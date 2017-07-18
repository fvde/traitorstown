package com.individual.thinking.traitorstown.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    @JoinColumn(name = "game_id")
    private Set<Player> players = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.OPEN;

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Integer getReadyPlayers(){
        return players.stream().filter(Player::getReady).collect(Collectors.toList()).size();
    }
}
