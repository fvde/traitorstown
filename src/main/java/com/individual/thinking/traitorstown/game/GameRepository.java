package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.GameStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface GameRepository extends CrudRepository<Game, Long> {
    List<Game> findByStatus(GameStatus status);
}
