package com.individual.thinking.traitorstown.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "game")
    private Set<Player> players = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.OPEN;
}
