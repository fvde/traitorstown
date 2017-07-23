package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@Getter
public class CardPlayed {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "turn_id")
    private Long turnId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_id")
    @NonNull
    private Card card;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    @NonNull
    private Player player;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "target_id")
    @NonNull
    private Player target;

    @Tolerate
    CardPlayed() {}
}
