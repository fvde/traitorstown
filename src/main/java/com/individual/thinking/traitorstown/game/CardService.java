package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.repository.DeckRepository;
import com.individual.thinking.traitorstown.model.Deck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final DeckRepository deckRepository;

    public List<Deck> getStandardDecks(){
        List<Deck> decks = new ArrayList<>();
        deckRepository.findAll().iterator().forEachRemaining(decks::add);
        return decks;
    }
}
