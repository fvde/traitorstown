package com.individual.thinking.traitorstown.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "game")
    private Set<Player> players;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.OPEN;
}
