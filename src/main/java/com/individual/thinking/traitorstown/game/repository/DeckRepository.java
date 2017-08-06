package com.individual.thinking.traitorstown.game.repository;

import com.individual.thinking.traitorstown.model.Deck;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckRepository extends CrudRepository<Deck, Long> {
}
