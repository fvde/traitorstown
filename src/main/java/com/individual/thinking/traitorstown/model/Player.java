package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.game.rules.Rules;
import com.individual.thinking.traitorstown.model.effects.Effect;
import com.individual.thinking.traitorstown.model.effects.EffectActive;
import com.individual.thinking.traitorstown.model.effects.EffectTargetType;
import com.individual.thinking.traitorstown.model.effects.SpecialEffectType;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import com.individual.thinking.traitorstown.model.exceptions.RuleSetViolationException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
    @JoinColumn(name = "target_id")
    @Builder.Default
    private List<EffectActive> activeEffects = new ArrayList<>();

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

        addEffect(CardService.Effects.get(SpecialEffectType.fromRole(role)), this);
        for (ResourceType type : ResourceType.values()){
            resources.put(type, Rules.STARTING_RESOURCES.containsKey(type) ? Rules.STARTING_RESOURCES.get(type) : 0);
        }

        deckCards.addAll(getDeckForRole(role).getCards());
        drawCards(Rules.INITIAL_AMOUNT_OF_CARDS);
    }

    public void startTurn(){
        drawCard();
    }

    public void drawCard(){
        drawCards(1);
    }

    public boolean mayPlayCard(Card card, Player target){
        return card.mayPlayCard(this, target);
    }

    public void playCard(Card card, Player target) throws PlayerDoesNotHaveCardException, RuleSetViolationException {
        if (!handCards.contains(card)){
            throw new PlayerDoesNotHaveCardException("You cannot play a card you don't have!");
        }

        if (!mayPlayCard(card, target)){
            throw new RuleSetViolationException("The requirements to play this card are not met");
        }

        card.getEffects().forEach(effect -> {
            if (effect.getEffectTargetType().equals(EffectTargetType.TARGET)){
                target.addEffect(effect, this);
            } else {
                this.addEffect(effect, this);
            }
        });

        if (!card.getSingleTurnOnly()){
            deckCards.add(card);
        }

        handCards.remove(card);
    }

    public void addEffect(Effect effect, Player origin){
        activeEffects.add(effect.toActive(origin, this));
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

    public void die(){
        deckCards.clear();
        handCards.clear();
    }

    public void discardSingleTurnCards(){
        handCards = handCards.stream().filter(Card::isNotSingleTurnOnly).collect(Collectors.toList());
    }

    public void applyEffects(Game game) {
        activeEffects.forEach(e -> e.apply(game));
    }

    public void removeInactiveEffects() {
        activeEffects = activeEffects.stream().filter(EffectActive::isActive).collect(Collectors.toList());
    }

    public Long getVotes(){
        return activeEffects.stream().filter(EffectActive::isVote).count();
    }

    public Long getKillVotes(){
        return activeEffects.stream().filter(EffectActive::isKillVote).count();
    }
    public Long getSpareVotes(){
        return activeEffects.stream().filter(EffectActive::isSpareVote).count();
    }

    public boolean isMayor(){
        return is(EffectActive::isMayor);
    }

    public boolean isCandidate(){
        return is(EffectActive::isCandidacy);
    }

    public boolean isOnTrial(){
        return is(EffectActive::isTrial);
    }

    public boolean isTraitor(){
        return is(EffectActive::isTraitor);
    }

    public boolean isCitizen(){
        return is(EffectActive::isCitizen);
    }

    public boolean isNotAtHome(){
        return is(EffectActive::isNotAtHome);
    }

    public boolean isHostingAParty() {return is(EffectActive::isParty);}

    public boolean isDead() {return is(EffectActive::isDeath);}

    public boolean isAlive() {return !isDead();}

    public boolean isHuman() {return !isAi();}

    public boolean isBeingVisitedForPartyByPlayer(Player origin) {return is(e -> e.isPartyWithGuest(origin));}

    public boolean hasNoPartyInvitation(){
        return !hasCardOfType(CardType.ATTEND_PARTY);
    }

    public boolean isTradingWith(Player origin) {return is(e -> e.isTradeFromPlayer(origin));}

    public Role getRole(){
        if (isCitizen()){ return Role.CITIZEN; }
        else if (isTraitor()){ return Role.TRAITOR; }
        else { return Role.NONE; }
    }

    private boolean is(Predicate<? super EffectActive> predicate){
        return activeEffects.stream().filter(predicate).findFirst().isPresent();
    }

    private boolean hasCardOfType(CardType type){
        return handCards.stream().filter(card -> card.getCardType().equals(type)).findFirst().isPresent();
    }

    public boolean is (Player other) {return getId().equals(other.getId());}
    public boolean isNot (Player other) {return !is(other);}

    @Tolerate
    Player () {}

    public void addResource(ResourceType type, Integer amount){
        resources.put(type, resources.get(type) + amount);
    }

    public void removeResource(ResourceType type, Integer amount){
        int mainResourceAmount = amount;

        if (type.equals(ResourceType.GOLD)){
            // traitors can spend stolen gold instead of gold
            if (isTraitor()){
                mainResourceAmount = Math.max(0, amount - resources.get(ResourceType.STOLEN_GOLD));
            }

            // citizen will only spend stolen gold randomly (on accident)
            if (isCitizen()){
                mainResourceAmount = Math.max(0, amount - ThreadLocalRandom.current().nextInt(Math.min(amount,  resources.get(ResourceType.STOLEN_GOLD) + 1)));
            }

            resources.put(ResourceType.STOLEN_GOLD, resources.get(ResourceType.STOLEN_GOLD) - (amount - mainResourceAmount));
        }

        resources.put(type, resources.get(type) - mainResourceAmount);
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

    public String getName(){
        return "Player " + getId();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
