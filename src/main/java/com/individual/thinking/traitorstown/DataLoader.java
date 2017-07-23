package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.game.repository.CardRepository;
import com.individual.thinking.traitorstown.game.repository.DeckRepository;
import com.individual.thinking.traitorstown.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public void run(ApplicationArguments args) {

        if (deckRepository.findAll().iterator().hasNext()){
            return;
        }

        List<Card> mainCards = Arrays.asList(
            Card.builder().name("Use Connections").description("Draw an additional card.").effects(
                    Arrays.asList(Effect.builder().targetType(Resource.CARD).type(EffectType.ADD).amount(1).duration(1).build(),
                            Effect.builder().targetType(Resource.REPUTATION).type(EffectType.REMOVE).amount(5).duration(1).build())
            ).build(),
            Card.builder().name("Build Farm").description("Provides a low amount of gold for the next 2 weeks.").effects(
                    Arrays.asList(Effect.builder().targetType(Resource.GOLD).type(EffectType.ADD).amount(1).duration(14).build())
            ).build(),
            Card.builder().name("Go to the Tavern").description("Increase your reputation").effects(
                    Arrays.asList(
                            Effect.builder().targetType(Resource.REPUTATION).type(EffectType.ADD).amount(5).duration(1).build(),
                            Effect.builder().targetType(Resource.GOLD).type(EffectType.REMOVE).amount(1).duration(1).build())
            ).build(),
            Card.builder().name("Throw Party").description("Increase your popularity by a considerable amount").effects(
                    Arrays.asList(
                            Effect.builder().targetType(Resource.REPUTATION).type(EffectType.ADD).amount(15).duration(1).build(),
                            Effect.builder().targetType(Resource.GOLD).type(EffectType.REMOVE).amount(5).duration(1).build())
            ).build(),
            Card.builder().name("Honest Trade").description("Receive a small amount of gold during the next week").effects(
                    Arrays.asList(
                            Effect.builder().targetType(Resource.GOLD).type(EffectType.ADD).amount(2).duration(7).build())
            ).build(),
            Card.builder().name("Dishonest Trade").description("Receive a decent amount of gold during the next week, but suffer a loss of reputation").effects(
                    Arrays.asList(
                            Effect.builder().targetType(Resource.GOLD).type(EffectType.ADD).amount(3).duration(7).build(),
                            Effect.builder().targetType(Resource.REPUTATION).type(EffectType.REMOVE).amount(1).duration(7).build())
            ).build(),
            Card.builder().name("Run for Mayor").description("Become mayor for one full week to win the game for your team. The mayor also has additional powers.").effects(
                    Arrays.asList(
                            Effect.builder().targetType(Resource.REPUTATION).type(EffectType.REMOVE).amount(10).duration(1).build(),
                            Effect.builder().targetType(Resource.REPUTATION).type(EffectType.REMOVE).amount(10).duration(1).build())
            ).build());

        cardRepository.save(mainCards);

        buildDeckForRole(Role.CITIZEN, mainCards);
        buildDeckForRole(Role.TRAITOR, mainCards);
    }

    private void buildDeckForRole(Role role, List<Card> cards){

        Deck deck = Deck.builder().name(role.name()).cards(new ArrayList<>()).role(role).build();
        cards.forEach(deck.getCards()::add);
        cards.forEach(deck.getCards()::add);

        deckRepository.save(deck);
    }
}
