package com.individual.thinking.traitorstown.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
