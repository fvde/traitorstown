package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "game_id")
    @Setter
    private Long gameId;

    @Enumerated(EnumType.STRING)
    @Setter
    private Role role = Role.NONE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "deck_id")
    @Setter
    private Deck deck;

    @ManyToMany
    @JoinTable(name = "player_card", joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"))
    private List<Card> cards = new ArrayList<>();

    @Setter
    private Boolean ready = false;

    @Tolerate
    Player () {}
}
