package com.individual.thinking.traitorstown.game.repository;

import com.individual.thinking.traitorstown.model.Turn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TurnRepository extends CrudRepository<Turn, Long> {
    Optional<Turn> findByGameIdAndCounter(Long gameId, Integer counter);
}
