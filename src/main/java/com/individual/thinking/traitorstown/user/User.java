package com.individual.thinking.traitorstown.user;

import com.individual.thinking.traitorstown.model.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique=true)
    private String email;

    private String password;

    @Column(unique=true)
    private String token;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    private Player player;

    @Tolerate
    User() {}
}
