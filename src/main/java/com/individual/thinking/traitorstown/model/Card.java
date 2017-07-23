package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
public class Card {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "card_effect", joinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "effect_id", referencedColumnName = "id"))
    private List<Effect> effects = new ArrayList<>();

    @Tolerate
    Card() {}

    public boolean mayPlayCard(Player player){
        return effects.stream().allMatch(effect -> effect.mayApply(player));
    }
}
