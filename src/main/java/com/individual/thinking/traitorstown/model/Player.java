package com.individual.thinking.traitorstown.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    private Boolean ready = false;
}
