package com.individual.thinking.traitorstown.game.repository;

import com.individual.thinking.traitorstown.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
}
