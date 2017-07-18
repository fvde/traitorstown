package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@Getter
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
    private String email;

    private String password;

    @Column(unique=true)
    private String token;

    @OneToOne(cascade = CascadeType.ALL)
    private Player player;

    @Tolerate
    User() {}
}
