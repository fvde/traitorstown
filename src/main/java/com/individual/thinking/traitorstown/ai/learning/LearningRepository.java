package com.individual.thinking.traitorstown.ai.learning;

import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LearningRepository {

    private final LearningDataManager learningDataManager;

    @Autowired
    public LearningRepository(TraitorsTownConfiguration configuration){
        learningDataManager = new LearningDataManager(configuration.getLearningEnabled());
    }

    public void save(Learning<GameState, Integer, DiscreteSpace, IDQN> learning){
        learningDataManager.save(learning);
    }

    public Policy<GameState, Integer> load(){
        return new DQNPolicy<>(learningDataManager.load());
    }

    public DataManager getDataManager(){
        return learningDataManager;
    }
}
