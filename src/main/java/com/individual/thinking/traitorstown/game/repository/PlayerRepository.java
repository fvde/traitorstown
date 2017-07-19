package com.individual.thinking.traitorstown.game.repository;

import com.individual.thinking.traitorstown.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {
}
