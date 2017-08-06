package com.individual.thinking.traitorstown.model;

import lombok.*;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@ToString
public class Card {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "card_effect", joinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "effect_id", referencedColumnName = "id"))
    private List<Effect> effects = new ArrayList<>();

    @Setter
    @NonNull
    @Builder.Default
    private boolean isSpecial = false;

    @NonNull
    private int version;

    @Tolerate
    Card() {}

    public boolean mayPlayCard(Player player){
        return effects.stream().allMatch(effect -> effect.mayApply(player));
    }

    public void updateCard(Card newCard){
        name = newCard.name;
        description = newCard.description;
        effects = newCard.effects;
        isSpecial = newCard.isSpecial;
        version = newCard.version;
    }
}
