package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.individual.thinking.traitorstown.util.CollectorsExtension.singletonCollector;

@Entity
@Builder
@Getter
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "game_id")
    private Long gameId;

    @Enumerated(EnumType.STRING)
    private Role role = Role.NONE;

    @ManyToMany
    @JoinTable(name = "player_deck", joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "deck_id", referencedColumnName = "id"))
    private List<Deck> decks = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "player_deck_card", joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"))
    private List<Card> deckCards = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "player_hand_card", joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"))
    private List<Card> handCards = new ArrayList<>();

    @Getter(value = AccessLevel.PRIVATE)
    private Integer gold = 0;

    @Getter(value = AccessLevel.PRIVATE)
    private Integer reputation = 0;

    @Setter
    @NonNull
    private boolean ready;

    @Setter
    @NonNull
    @Builder.Default
    private boolean ai = false;

    public void startGameWithRole(Role role){
        this.role = role;
        deckCards.clear();
        handCards.clear();
        gold = Configuration.INITIAL_AMOUNT_OF_GOLD;
        reputation = Configuration.INITIAL_AMOUNT_OF_REPUTATION;
        deckCards.addAll(getDeckForRole(role).getCards());
        drawCards(Configuration.INITIAL_AMOUNT_OF_CARDS);
    }

    public void drawCard(){
        drawCards(1);
    }

    public boolean mayPlayCard(Card card){
        return card.mayPlayCard(this);
    }

    public void playCard(Card card) throws PlayerDoesNotHaveCardException {
        if (!handCards.contains(card)){
            throw new PlayerDoesNotHaveCardException("You cannot play a card you don't have!");
        }
        handCards.remove(card);
        deckCards.add(card);
    }

    private Deck getDeckForRole(Role role){
        return decks.stream().filter(deck -> deck.getRole().equals(role)).collect(singletonCollector());
    }

    private void drawCards(Integer amount){
        // may not draw more cards than in deck
        amount = Math.min(amount, deckCards.size());

        // may not draw more cards than allowed to have at the same time
        int overDraw = handCards.size() + amount - Configuration.MAXIMUM_AMOUNT_OF_CARDS;
        if (overDraw > 0){
            amount = Math.max(amount - overDraw, 0);
        }

        List<Card> availableCards = new ArrayList<>(deckCards);
        Collections.shuffle(availableCards);
        List<Card> drawnCards = availableCards.subList(0, amount);
        handCards.addAll(drawnCards);
        drawnCards.forEach(deckCards::remove);
    }

    @Tolerate
    Player () {}

    public Integer getResource(Resource resource){
        switch (resource) {
            case GOLD: return gold;
            case REPUTATION: return reputation;
            case CARD: return handCards.size();
            case MAYOR: return 0;
            default: return null;
        }
    }

    public void setResource(Resource resource, Integer value){
        switch (resource) {
            case GOLD: {
                gold = value;
                break;
            }
            case REPUTATION: {
                reputation = value;
                break;
            }
            case CARD: {
                drawCards(Math.max(value - handCards.size(), 0));
                break;
            }
            case MAYOR: {
                //TODO add mayor cards
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "role=" + role +
                ", ai=" + ai +
                ", handCards=" + handCards +
                ", gold=" + gold +
                ", reputation=" + reputation +
                '}';
    }
}
