package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.exceptions.CardNotFoundException;
import com.individual.thinking.traitorstown.game.repository.CardRepository;
import com.individual.thinking.traitorstown.game.repository.DeckRepository;
import com.individual.thinking.traitorstown.game.repository.EffectRepository;
import com.individual.thinking.traitorstown.model.*;
import com.individual.thinking.traitorstown.model.effects.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.individual.thinking.traitorstown.Configuration.TOTAL_NUMBER_OF_CARDS;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardService {

    public static Map<CardType, Card> Cards = new HashMap<>();
    public static Map<SpecialEffectType, Effect> Effects = new HashMap<>();

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

    public void initialize() {
        List<Card> mainCards = Arrays.asList(
//                createCardOfType(CardType.CONNECTIONS,
//                        Card.builder().cardType(CardType.CONNECTIONS).name("Use Connections").description("Draw an additional card.").effects(
//                        Arrays.asList(
//                                DrawCardEffect.builder().build(),
//                                ResourceEffect.builder().resourceType(ResourceType.REPUTATION).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(5).duration(1).build())
//                ).build()),
//                createCardOfType(CardType.PARTY,
//                Card.builder().cardType(CardType.PARTY).name("Throw Party").description("Increase your popularity by a considerable amount").effects(
//                        Arrays.asList(
//                                ResourceEffect.builder().resourceType(ResourceType.REPUTATION).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.TARGET).amount(15).duration(1).build(),
//                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(5).duration(1).build())
//                ).build()),
//                createCardOfType(CardType.HONEST_TRADE,
//                Card.builder().cardType(CardType.HONEST_TRADE).name("Honest Trade").description("Receive a small amount of gold during the next week").effects(
//                        Arrays.asList(
//                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.TARGET).amount(3).duration(7).build())
//                ).build()),
//                createCardOfType(CardType.DISHONEST_TRADE,
//                Card.builder().cardType(CardType.DISHONEST_TRADE).name("Dishonest Trade").description("Receive a decent amount of gold during the next week, but suffer a loss of reputation").effects(
//                        Arrays.asList(
//                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.TARGET).amount(5).duration(7).build(),
//                                ResourceEffect.builder().resourceType(ResourceType.REPUTATION).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(1).duration(7).build())
//                ).build()),
                createCardOfType(CardType.RUN_FOR_MAYOR,
                Card.builder().version(1).cardType(CardType.RUN_FOR_MAYOR).name("Run for Mayor").description("Become mayor on election day. Find and put all traitors on trial!")
                        .effects(Arrays.asList(
                                CandidacyEffect.builder().build(),
                                ResourceEffect.builder().resourceType(ResourceType.REPUTATION).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(10).duration(1).build(),
                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(10).duration(1).build())
                ).build()));

        List<Card> citizenCards = Arrays.asList(
                createCardOfType(CardType.FARM,
                        Card.builder().cardType(CardType.FARM).name("Build Farm").description("Provides a low amount of gold for the next week.").effects(
                                Arrays.asList(
                                        ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.TARGET).amount(1).duration(7).build())
                        ).build())
        );

        List<Card> traitorCards = Arrays.asList(
                createCardOfType(CardType.ROBBERY,
                        Card.builder().cardType(CardType.ROBBERY).name("Robbery").description("Steal a players gold. Very risky if the target is at home!").effects(
                                Arrays.asList(
                                        RobberyEffect.builder().build(),
                                        NotAtHomeEffect.builder().effectTargetType(EffectTargetType.SELF).duration(1).build())
                        ).build())
                );

        // SPECIAL CARDS
        createCardOfType(CardType.VOTE,
                Card.builder().cardType(CardType.VOTE).name("Vote").description("Vote for a player to become mayor.").singleTurnOnly(true)
                        .effects(Arrays.asList(
                                VoteEffect.builder().duration(2).build())
        ).build());

        // SPECIAL EFFECTS

        createEffect(SpecialEffectType.CITIZEN, CitizenEffect.builder().build());

        createEffect(SpecialEffectType.TRAITOR, TraitorEffect.builder().build());

        createEffect(SpecialEffectType.MAYOR, MayorEffect.builder().duration(7).build());

        if (TOTAL_NUMBER_OF_CARDS != cardRepository.count()){
            throw new IllegalArgumentException("Incorrect number of total cards");
        }

        if (deckRepository.count() == 0){
            // TODO version decks
            buildDeckForRole(Role.CITIZEN, Stream.concat(
                    mainCards.stream(),
                    citizenCards.stream())
                    .collect(Collectors.toList()));

            buildDeckForRole(Role.TRAITOR,
                    Stream.concat(
                            mainCards.stream(),
                            traitorCards.stream())
                    .collect(Collectors.toList()));
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

    private Effect createEffect(SpecialEffectType type, Effect effect){
        effectRepository.save(effect);
        Effects.put(type, effect);
        return effect;
    }

    private void buildDeckForRole(Role role, List<Card> cards){

        Deck deck = Deck.builder().name(role.name()).cards(new ArrayList<>()).role(role).build();
        cards.forEach(deck.getCards()::add);
        cards.forEach(deck.getCards()::add);

        deckRepository.save(deck);
    }
}
