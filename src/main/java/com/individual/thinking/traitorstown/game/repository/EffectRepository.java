package com.individual.thinking.traitorstown.game.repository;

import com.individual.thinking.traitorstown.model.Effect;
import com.individual.thinking.traitorstown.model.EffectType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EffectRepository extends CrudRepository<Effect, Long> {
    Optional<Effect> findByEffectType(EffectType type);
}
