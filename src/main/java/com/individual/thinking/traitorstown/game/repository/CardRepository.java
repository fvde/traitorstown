package com.individual.thinking.traitorstown.game.repository;

import com.individual.thinking.traitorstown.model.Card;
import com.individual.thinking.traitorstown.model.CardType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends CrudRepository<Card, Long> {
    Optional<Card> findByCardType(CardType type);
}
