package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.game.rules.Rules;
import com.individual.thinking.traitorstown.model.effects.Effect;
import com.individual.thinking.traitorstown.model.effects.EffectActive;
import com.individual.thinking.traitorstown.model.effects.EffectType;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.individual.thinking.traitorstown.util.CollectorsExtension.singletonCollector;

@Entity
@Builder
@Getter
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id")
    private Long gameId;

    @ManyToMany
    @JoinTable(name = "player_deck", joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "deck_id", referencedColumnName = "id"))
    private List<Deck> decks = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "player_deck_card", joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"))
    @Builder.Default
    private List<Card> deckCards = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "player_hand_card", joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "card_id", referencedColumnName = "id"))
    @Builder.Default
    private List<Card> handCards = new ArrayList<>();


    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "player_id")
    @Builder.Default
    private List<EffectActive> activeEffects = new ArrayList<>();

    @Getter(value = AccessLevel.PRIVATE)
    private Integer gold = 0;

    @Getter(value = AccessLevel.PRIVATE)
    private Integer reputation = 0;

    @ElementCollection
    @CollectionTable(name="resources", joinColumns=@JoinColumn(name="player_id", referencedColumnName = "id"))
    @MapKeyColumn(name = "resource")
    @MapKeyEnumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "amount")
    private Map<ResourceType, Integer> resources = new HashMap<>();

    @Setter
    @NonNull
    private boolean ready;

    @Setter
    @NonNull
    @Builder.Default
    private boolean ai = false;

    public void startGameWithRole(Role role){

        activeEffects.clear();
        deckCards.clear();
        handCards.clear();

        addEffect(CardService.Effects.get(EffectType.fromRole(role)));
        for (ResourceType type : ResourceType.values()){
            resources.put(type, Rules.STARTING_RESOURCES.containsKey(type) ? Rules.STARTING_RESOURCES.get(type) : 0);
        }

        deckCards.addAll(getDeckForRole(role).getCards());
        drawCards(Rules.INITIAL_AMOUNT_OF_CARDS);
    }

    public void drawCard(){
        drawCards(1);
    }

    public boolean mayPlayCard(Card card){
        return card.mayPlayCard(this);
    }

    public void playCard(Card card, Player target) throws PlayerDoesNotHaveCardException {
        if (!handCards.contains(card)){
            throw new PlayerDoesNotHaveCardException("You cannot play a card you don't have!");
        }

        card.getEffects().forEach(effect -> target.addEffect(effect));

        if (!card.getSingleTurnOnly()){
            deckCards.add(card);
        }

        handCards.remove(card);
    }

    public void addEffect(Effect effect){
        addEffect(effect, this);
    }

    private void addEffect(Effect effect, Player target){
        activeEffects.add(EffectActive.builder()
                .effect(effect)
                .player(this)
                .target(target)
                .remainingTurns(effect.getDuration())
                .build());
    }

    private Deck getDeckForRole(Role role){
        return decks.stream().filter(deck -> deck.getRole().equals(role)).collect(singletonCollector());
    }

    public void drawCards(Integer amount){
        // may not draw more cards than in deck
        amount = Math.min(amount, deckCards.size());

        // may not draw more cards than allowed to have at the same time
        int overDraw = handCards.size() + amount - Rules.MAXIMUM_NUMBER_OF_CARDS;
        if (overDraw > 0){
            amount = Math.max(amount - overDraw, 0);
        }

        List<Card> availableCards = new ArrayList<>(deckCards);
        Collections.shuffle(availableCards);
        List<Card> drawnCards = availableCards.subList(0, amount);
        handCards.addAll(drawnCards);
        drawnCards.forEach(deckCards::remove);
    }

    public void addCard(Card card){
        handCards.add(card);
    }

    public void endTurn() {
        activeEffects.stream().forEach(EffectActive::apply);
        activeEffects = activeEffects.stream().filter(EffectActive::isActive).collect(Collectors.toList());
        handCards = handCards.stream().filter(card -> !card.getSingleTurnOnly()).collect(Collectors.toList());
    }

    public Long getVotes(){
        return activeEffects.stream().filter(EffectActive::isVote).count();
    }

    public boolean isMayor(){
        return is(EffectActive::isMayor);
    }

    public boolean isCandidate(){
        return is(EffectActive::isCandidacy);
    }

    public Role getRole(){
        if (is(EffectActive::isCitizen)){ return Role.CITIZEN; }
        else if (is(EffectActive::isTraitor)){ return Role.TRAITOR; }
        else { return Role.NONE; }
    }

    private boolean is(Predicate<? super EffectActive> predicate){
        return activeEffects.stream().filter(predicate).findFirst().isPresent();
    }

    @Tolerate
    Player () {}

    public void addResource(ResourceType type, Integer amount){
        resources.put(type, resources.get(type) + amount);
    }

    public void removeResource(ResourceType type, Integer amount){
        resources.put(type, resources.get(type) - amount);
    }

    public Integer getResource(ResourceType resourceType){
        return resources.get(resourceType);
    }

    public void clearCandidacy() {
        activeEffects.removeAll(activeEffects.stream().filter(EffectActive::isCandidacy).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
