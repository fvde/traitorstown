package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
public class Deck {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "deck_card", joinColumns = @JoinColumn(name = "deck_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"))
    private List<Card> cards = new ArrayList<>();

    @Tolerate
    Deck() {}
}
