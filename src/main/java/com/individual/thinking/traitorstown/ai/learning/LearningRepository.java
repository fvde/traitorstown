package com.individual.thinking.traitorstown.ai.learning;

import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public class LearningRepository {

    private static final String LEARNING_NAME = "traitorstown.training";
    public static final String ROOT_LEARNING_DIR = "learning";

    private final DataManager dataManager;

    @Autowired
    public LearningRepository(TraitorsTownConfiguration configuration){
        dataManager = new DataManager(ROOT_LEARNING_DIR, configuration.getLearningEnabled());
    }

    public void save(Learning<GameState, Integer, DiscreteSpace, IDQN> learning){
        dataManager.save(dataManager.getModelDir() + "/" + LEARNING_NAME, learning);
    }

    public Policy<GameState, Integer> load(){
        return new DQNPolicy<>(dataManager.load(new File(dataManager.getModelDir() + "/" + LEARNING_NAME), QLearning.QLConfiguration.class).getFirst());
    }

    public DataManager getDataManager(){
        return dataManager;
    }
}
