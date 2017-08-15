package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.exceptions.CardNotFoundException;
import com.individual.thinking.traitorstown.game.repository.CardRepository;
import com.individual.thinking.traitorstown.game.repository.DeckRepository;
import com.individual.thinking.traitorstown.game.repository.EffectRepository;
import com.individual.thinking.traitorstown.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.individual.thinking.traitorstown.Configuration.TOTAL_NUMBER_OF_CARDS;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardService {

    public static Map<CardType, Card> Cards = new HashMap<>();
    public static Map<EffectType, Effect> Effects = new HashMap<>();

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final EffectRepository effectRepository;

    public List<Deck> getStandardDecks(){
        List<Deck> decks = new ArrayList<>();
        deckRepository.findAll().iterator().forEachRemaining(decks::add);
        return decks;
    }

    protected Card getCardById(Long id) throws CardNotFoundException {
        Card card = cardRepository.findById(id).get();
        if (card == null){
            throw new CardNotFoundException("Card not found");
        }
        return card;
    }

    public void initializeCards() {
        List<Card> mainCards = Arrays.asList(
                createCardOfType(CardType.CONNECTIONS,
                        Card.builder().cardType(CardType.CONNECTIONS).name("Use Connections").description("Draw an additional card.").effects(
                        Arrays.asList(
                                Effect.builder().targetResource(Resource.CARD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(1).duration(1).build(),
                                Effect.builder().targetResource(Resource.REPUTATION).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SINGLE).amount(5).duration(1).build())
                ).build()),
                createCardOfType(CardType.FARM,
                        Card.builder().cardType(CardType.FARM).name("Build Farm").description("Provides a low amount of gold for the next 2 weeks.").effects(
                        Arrays.asList(
                                Effect.builder().targetResource(Resource.GOLD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(3).duration(14).build())
                ).build()),
                createCardOfType(CardType.TAVERN,
                Card.builder().cardType(CardType.TAVERN).name("Go to the Tavern").description("Increase your reputation").effects(
                        Arrays.asList(
                                Effect.builder().targetResource(Resource.REPUTATION).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(5).duration(1).build(),
                                Effect.builder().targetResource(Resource.GOLD).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SINGLE).amount(1).duration(1).build())
                ).build()),
                createCardOfType(CardType.PARTY,
                Card.builder().cardType(CardType.PARTY).name("Throw Party").description("Increase your popularity by a considerable amount").effects(
                        Arrays.asList(
                                Effect.builder().targetResource(Resource.REPUTATION).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(15).duration(1).build(),
                                Effect.builder().targetResource(Resource.GOLD).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SINGLE).amount(5).duration(1).build())
                ).build()),
                createCardOfType(CardType.HONEST_TRADE,
                Card.builder().cardType(CardType.HONEST_TRADE).name("Honest Trade").description("Receive a small amount of gold during the next week").effects(
                        Arrays.asList(
                                Effect.builder().targetResource(Resource.GOLD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(3).duration(7).build())
                ).build()),
                createCardOfType(CardType.DISHONEST_TRADE,
                Card.builder().cardType(CardType.DISHONEST_TRADE).name("Dishonest Trade").description("Receive a decent amount of gold during the next week, but suffer a loss of reputation").effects(
                        Arrays.asList(
                                Effect.builder().targetResource(Resource.GOLD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(5).duration(7).build(),
                                Effect.builder().targetResource(Resource.REPUTATION).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SINGLE).amount(1).duration(7).build())
                ).build()),
                createCardOfType(CardType.RUN_FOR_MAYOR,
                Card.builder().version(1).cardType(CardType.RUN_FOR_MAYOR).name("Run for Mayor").description("Become mayor on election day. Stay alive for one week to win the game.")
                        .messages(Collections.singletonList(Message.builder().structure(MessageStructure.PREFIX_TARGET).content(" just applied for the mayor position!").build()))
                        .effects(Arrays.asList(
                                Effect.builder().targetResource(Resource.CANDIDACY).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(1).duration(Integer.MAX_VALUE).visibility(Visibility.ALL).build(),
                                Effect.builder().targetResource(Resource.REPUTATION).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SINGLE).amount(10).duration(1).build(),
                                Effect.builder().targetResource(Resource.GOLD).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SINGLE).amount(10).duration(1).build())
                ).build()));

        // SPECIAL CARDS
        createCardOfType(CardType.VOTE,
                Card.builder().cardType(CardType.VOTE).name("Vote").description("Vote for a player to become mayor.").singleTurnOnly(true)
                        .effects(Arrays.asList(
                                Effect.builder().targetResource(Resource.VOTE).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(1).duration(2).visibility(Visibility.ALL).build())
        ).build());

        // SPECIAL EFFECTS
        createEffect(EffectType.CITIZEN,
                Effect.builder().effectType(EffectType.CITIZEN).targetResource(Resource.CITIZEN).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(1).duration(Integer.MAX_VALUE).visibility(Visibility.PLAYER).build());

        createEffect(EffectType.TRAITOR,
                Effect.builder().effectType(EffectType.TRAITOR).targetResource(Resource.TRAITOR).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(1).duration(Integer.MAX_VALUE).visibility(Visibility.FACTION).build());

        createEffect(EffectType.MAYOR,
                Effect.builder().effectType(EffectType.MAYOR).targetResource(Resource.MAYOR).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.SINGLE).amount(1).duration(7).visibility(Visibility.ALL).build());

        if (TOTAL_NUMBER_OF_CARDS != cardRepository.count()){
            throw new IllegalArgumentException("Incorrect number of total cards");
        }

        if (deckRepository.count() == 0){
            // TODO version decks
            buildDeckForRole(Role.CITIZEN, mainCards);
            buildDeckForRole(Role.TRAITOR, mainCards);
        }
    }

    private Card createCardOfType(CardType type, Card card){
        Optional<Card> existingCard = cardRepository.findByCardType(type);

        if (existingCard.isPresent()){
            Card currentCard = existingCard.get();
            if (currentCard.getVersion() != card.getVersion()){
                // TODO enable versioning of cards and effects
                log.info("NOT IMPLEMENTED: Found new version of card {}, updating to {}", currentCard, card);
                // currentCard.updateCard(card);
                // cardRepository.save(currentCard);
            } else {
                log.info("Found card {}, loading...", currentCard);
            }
            Cards.put(type, currentCard);
            return currentCard;
        } else {
            log.info("No version found for card {}, creating new...", card);
            cardRepository.save(card);
            Cards.put(type, card);
            return card;
        }
    }

    private Effect createEffect(EffectType type, Effect effect){
        Optional<Effect> existingEffect = effectRepository.findByEffectType(type);

        if (existingEffect.isPresent()){
            Effects.put(type, existingEffect.get());
            return existingEffect.get();
        } else {
            effectRepository.save(effect);
            Effects.put(type, effect);
            return effect;
        }
    }

    private void buildDeckForRole(Role role, List<Card> cards){

        Deck deck = Deck.builder().name(role.name()).cards(new ArrayList<>()).role(role).build();
        cards.forEach(deck.getCards()::add);
        cards.forEach(deck.getCards()::add);

        deckRepository.save(deck);
    }
}
