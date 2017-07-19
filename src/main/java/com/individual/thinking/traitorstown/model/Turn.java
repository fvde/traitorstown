package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Builder
@Getter
public class Turn {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "game_id")
    private Long gameId;

    private Integer counter;

    @Tolerate
    Turn () {}
}
