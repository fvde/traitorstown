package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.exceptions.CardNotFoundException;
import com.individual.thinking.traitorstown.game.repository.CardRepository;
import com.individual.thinking.traitorstown.game.repository.DeckRepository;
import com.individual.thinking.traitorstown.model.Card;
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
    private final CardRepository cardRepository;

    public List<Deck> getStandardDecks(){
        List<Deck> decks = new ArrayList<>();
        deckRepository.findAll().iterator().forEachRemaining(decks::add);
        return decks;
    }

    protected Card getCardById(Long id) throws CardNotFoundException {
        Card card = cardRepository.findOne(id);
        if (card == null){
            throw new CardNotFoundException("Card not found");
        }
        return card;
    }
}
