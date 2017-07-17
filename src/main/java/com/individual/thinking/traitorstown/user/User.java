package com.individual.thinking.traitorstown.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
    private final String email;
    private final String password;
}
