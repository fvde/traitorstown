package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Player;
import org.springframework.data.repository.CrudRepository;

interface PlayerRepository extends CrudRepository<Player, Long> {
}
