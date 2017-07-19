package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.game.repository.CardRepository;
import com.individual.thinking.traitorstown.game.repository.DeckRepository;
import com.individual.thinking.traitorstown.model.Card;
import com.individual.thinking.traitorstown.model.Deck;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public void run(ApplicationArguments args) {

        deckRepository.deleteAll();
        cardRepository.deleteAll();

        Iterable<Card> mainCards = cardRepository.save(Arrays.asList(
                Card.builder().name("Throw Party").build(),
                Card.builder().name("Attend Party").build(),
                Card.builder().name("Honest Trade").build(),
                Card.builder().name("Dishonest Trade").build(),
                Card.builder().name("Run for Mayor").build()));

        Deck traitorDeck = Deck.builder().name("Traitor").cards(new ArrayList<>()).build();
        mainCards.forEach(traitorDeck.getCards()::add);
        mainCards.forEach(traitorDeck.getCards()::add);

        Deck citizenDeck = Deck.builder().name("Citizen").cards(new ArrayList<>()).build();
        mainCards.forEach(citizenDeck.getCards()::add);
        mainCards.forEach(citizenDeck.getCards()::add);

        deckRepository.save(Arrays.asList(citizenDeck, traitorDeck));

        Configuration.CITIZEN_DECK = citizenDeck;
        Configuration.TRAITOR_DECK = traitorDeck;
    }
}
