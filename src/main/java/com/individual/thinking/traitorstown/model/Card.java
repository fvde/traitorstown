package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.model.effects.Effect;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "card_effect", joinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "effect_id", referencedColumnName = "id"))
    private List<Effect> effects = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "card_message", joinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"))
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @NonNull
    @Builder.Default
    private Boolean singleTurnOnly = false;

    @NonNull
    @Builder.Default
    private Integer version = 0;

    @Tolerate
    Card() {}

    public boolean mayPlayCard(Player player){
        return effects.stream().allMatch(effect -> effect.mayApply(player));
    }

    public void updateCard(Card newCard){
        name = newCard.name;
        description = newCard.description;
        effects = newCard.effects;
        singleTurnOnly = newCard.singleTurnOnly;
        version = newCard.version;
    }
}
