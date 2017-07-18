package com.individual.thinking.traitorstown.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    @JoinColumn(name = "game_id")
    private List<Player> players = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.OPEN;

    public void addPlayer(Player player){
        if (!players.contains(player)){
            players.add(player);
        }
    }
}
