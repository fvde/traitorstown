package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.game.repository.CardRepository;
import com.individual.thinking.traitorstown.game.repository.DeckRepository;
import com.individual.thinking.traitorstown.model.Card;
import com.individual.thinking.traitorstown.model.Deck;
import com.individual.thinking.traitorstown.model.Role;
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
            Card.builder().name("Throw Party").build(),
            Card.builder().name("Attend Party").build(),
            Card.builder().name("Honest Trade").build(),
            Card.builder().name("Dishonest Trade").build(),
            Card.builder().name("Run for Mayor").build());

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
