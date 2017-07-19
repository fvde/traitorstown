package com.individual.thinking.traitorstown.game.repository;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {
    List<Game> findByStatus(GameStatus status);
}
