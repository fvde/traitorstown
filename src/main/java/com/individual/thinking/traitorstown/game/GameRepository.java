package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Game;
import org.springframework.data.repository.CrudRepository;

interface GameRepository extends CrudRepository<Game, Long> {
}
