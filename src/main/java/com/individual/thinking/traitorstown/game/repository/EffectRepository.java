package com.individual.thinking.traitorstown.game.repository;

import com.individual.thinking.traitorstown.model.effects.Effect;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EffectRepository extends CrudRepository<Effect, Long> {
    //Optional<Effect> findByEffectType(EffectType type);
}
