package com.individual.thinking.traitorstown.user;

import com.individual.thinking.traitorstown.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token);
}
