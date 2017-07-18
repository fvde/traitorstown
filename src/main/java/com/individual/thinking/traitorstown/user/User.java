package com.individual.thinking.traitorstown.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Builder
@Getter
class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
    private String email;

    @Getter(AccessLevel.PACKAGE)
    private String password;

    @Column(unique=true)
    private String token;


    @Tolerate
    User() {}
}
