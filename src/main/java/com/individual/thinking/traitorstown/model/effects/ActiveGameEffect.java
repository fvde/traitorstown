package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Game;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@Getter
@ToString
public class ActiveGameEffect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "effect_id")
    @NonNull
    private GlobalEffect effect;

    @Column(name = "game_id")
    private Long gameId;

    @NonNull
    private Integer remainingTurns;

    @Tolerate
    ActiveGameEffect() {}

    public void apply(Game game){
        effect.apply(game);
        remainingTurns--;
    }
}
