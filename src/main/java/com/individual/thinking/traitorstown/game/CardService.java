package com.individual.thinking.traitorstown.game;

import com.google.common.collect.Streams;
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
import static java.util.Arrays.asList;

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
        List<Card> mainCards = asList(
                createCardOfType(CardType.PARTY,
                        Card.builder().cardType(CardType.PARTY).name("Throw Party").description("Invite all honest citizens. Increases your " + ResourceType.REPUTATION.name().toLowerCase() + ".").effects(asList(
                                PartyEffect.builder().build(),
                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(3).duration(1).build())
                        ).build()),
                createCardOfType(CardType.TRADE,
                        Card.builder().cardType(CardType.TRADE).name("Trade").description("Receive a good amount of " + ResourceType.GOLD.name().toLowerCase() + " if the target trades with you.").effects(asList(
                                TradeEffect.builder().duration(7).build())
                        ).build()),
//                createCardOfType(CardType.DISHONEST_TRADE,
//                Card.builder().cardType(CardType.DISHONEST_TRADE).name("Dishonest Trade").description("Receive a decent amount of gold during the next week, but suffer a loss of reputation").effects(
//                        Arrays.asList(
//                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.TARGET).amount(5).duration(7).build(),
//                                ResourceEffect.builder().resourceType(ResourceType.REPUTATION).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(1).duration(7).build())
//                ).build()),
                createCardOfType(CardType.RUN_FOR_MAYOR,
                        Card.builder().version(1).cardType(CardType.RUN_FOR_MAYOR).name("Run for Mayor").description("Become mayor on election day. Find and put all traitors on trial!").effects(asList(
                                CandidacyEffect.builder().build(),
                                ResourceEffect.builder().resourceType(ResourceType.REPUTATION).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(5).duration(1).build(),
                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(1).duration(1).build())
                ).build()));

        List<Card> citizenCards = asList(
                createCardOfType(CardType.FARM,
                        Card.builder().cardType(CardType.FARM).name("Build Farm").description("Provides a low amount of " + ResourceType.GOLD.name().toLowerCase() + " for the next week.").effects(asList(
                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.ADD).effectTargetType(EffectTargetType.TARGET).amount(1).duration(7).build())
                        ).build())
        );

        List<Card> traitorCards = asList(
                createCardOfType(CardType.ROBBERY,
                        Card.builder().cardType(CardType.ROBBERY).name("Robbery").description("Steal a players " + ResourceType.GOLD.name().toLowerCase() + ". Very risky if the target is at home!").effects(asList(
                                RobberyEffect.builder().build(),
                                NotAtHomeEffect.builder().effectTargetType(EffectTargetType.SELF).duration(1).build())
                        ).build()),
                createCardOfType(CardType.MURDER,
                        Card.builder().cardType(CardType.MURDER).name("Murder").description("Murder a citizen.").effects(asList(
                                MurderEffect.builder().build(),
                                NotAtHomeEffect.builder().effectTargetType(EffectTargetType.SELF).duration(1).build(),
                                ResourceEffect.builder().resourceType(ResourceType.GOLD).operator(EffectOperator.REMOVE).effectTargetType(EffectTargetType.SELF).amount(1).duration(1).build())
                        ).build())
                );

        List<Card> mayorCards = asList(
                createCardOfType(CardType.TRIAL,
                        Card.builder().cardType(CardType.TRADE).name("Trial").description("Put target player on trial, so they may be judged for their sins.").effects(asList(
                                TrialEffect.builder().build())
                        ).build())
        );

        // SPECIAL CARDS
        createCardOfType(CardType.VOTE,
                Card.builder().cardType(CardType.VOTE).name("Vote").description("Vote for a player to become mayor.")
                        .singleTurnOnly(true)
                        .effects(asList(
                                VoteEffect.builder().build())
                        ).build());

        createCardOfType(CardType.VOTE_KILL,
                Card.builder().cardType(CardType.VOTE_KILL).name("Vote Kill").description("The accused is clearly a traitor. He should die!")
                        .singleTurnOnly(true)
                        .effects(asList(
                                VoteKillEffect.builder().build())
                        ).build());

        createCardOfType(CardType.VOTE_SPARE,
                Card.builder().cardType(CardType.VOTE_SPARE).name("Vote Spare").description("Never has there been a more honorable citizen. He should live!")
                        .singleTurnOnly(true)
                        .effects(asList(
                                VoteSpareEffect.builder().build())
                        ).build());

        createCardOfType(CardType.ATTEND_PARTY,
                Card.builder().cardType(CardType.ATTEND_PARTY).name("Attend Party").description("Attend party for increased " + ResourceType.REPUTATION.name().toLowerCase() + " and an alibi for the night....")
                        .singleTurnOnly(true)
                        .effects(
                        asList(
                                AttendPartyEffect.builder().build(),
                                NotAtHomeEffect.builder().duration(1).effectTargetType(EffectTargetType.SELF).build())
                        ).build());

        // SPECIAL EFFECTS

        createEffect(SpecialEffectType.CITIZEN, CitizenEffect.builder().build());

        createEffect(SpecialEffectType.TRAITOR, TraitorEffect.builder().build());

        createEffect(SpecialEffectType.MAYOR, MayorEffect.builder().duration(7).build());

        createEffect(SpecialEffectType.DEATH, DeathEffect.builder().build());

        createEffect(SpecialEffectType.ELECTIONS, ElectionsEffect.builder().build());

        if (TOTAL_NUMBER_OF_CARDS != cardRepository.count()){
            throw new IllegalArgumentException("Incorrect number of total cards");
        }

        if (deckRepository.count() == 0){
            // TODO version decks
            buildDeckForRole(Role.CITIZEN,
                    Stream.of(
                        mainCards.stream(),
                        citizenCards.stream(),
                        mayorCards.stream())
                    .flatMap(Streams::concat)
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
