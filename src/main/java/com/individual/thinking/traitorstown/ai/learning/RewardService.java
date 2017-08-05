package com.individual.thinking.traitorstown.ai.learning;

import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RewardService {

    final double WIN_WEIGHT = 10000.0;
    final double NEW_TURN_REWARD = 1.0;
    final double NUMBER_OF_TURNS_PENALITY = 10.0;

    public double getReward(GameState state, Integer turn) {
        double reward = 0.0;

        // reward successful turn
        reward += NEW_TURN_REWARD;

        // reward victory
        reward += state.isWinner() ? (WIN_WEIGHT - (NUMBER_OF_TURNS_PENALITY * turn)) : 0.0;
        return reward;
    }
}
