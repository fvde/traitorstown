package com.individual.thinking.traitorstown.ai.learning;

import com.individual.thinking.traitorstown.model.Game;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.Policy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;

@Slf4j
public class ReinforcementLearningRunner {

    public static QLearning.QLConfiguration TRAITORS_QL =
            new QLearning.QLConfiguration(
                    123,   //Random seed
                    100000,//Max step By epoch
                    80000, //Max step
                    10000, //Max size of experience replay
                    32,    //size of batches
                    100,   //target update (hard)
                    0,     //num step noop warmup
                    0.05,  //reward scaling
                    0.99,  //gamma
                    10.0,  //td-error clipping
                    0.1f,  //min epsilon
                    2000,  //num step for eps greedy anneal
                    true   //double DQN
            );


    public static AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration TRAITORS_ASYNC_QL =
            new AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration(
                    123,        //Random seed
                    100000,     //Max step By epoch
                    80000,      //Max step default 80000
                    8,          //Number of threads
                    5,          //t_max
                    100,        //target update (hard)
                    0,          //num step noop warmup
                    0.1,        //reward scaling
                    0.99,       //gamma
                    10.0,       //td-error clipping
                    0.1f,       //min epsilon
                    2000        //num step for eps greedy anneal
            );

    private static A3CDiscrete.A3CConfiguration TRAITORS_A3C =
            new A3CDiscrete.A3CConfiguration(
                    123,            //Random seed
                    200,            //Max step By epoch
                    500000,         //Max step
                    16,              //Number of threads
                    5,              //t_max
                    10,             //num step noop warmup
                    0.01,           //reward scaling
                    0.99,           //gamma
                    10.0           //td-error clipping
            );

    public static DQNFactoryStdDense.Configuration TRAITORS_NET =
            DQNFactoryStdDense.Configuration.builder()
                    .l2(0.01).learningRate(1e-2).numLayer(3).numHiddenNodes(16).build();

    public static void main(String[] args ){
        trainAI();
    }

    private static void trainAI() {
        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(true);

        //define the mdp from toy (toy length)
        TraitorsTownMDP mdp = new TraitorsTownMDP(10, 8, false);

        //define the training method
        Learning<Game, Integer, DiscreteSpace, IDQN> dql = new QLearningDiscreteDense<>(mdp, TRAITORS_NET, TRAITORS_QL, manager);

        //start the training
        dql.train();

        Policy<Game, Integer> policy = dql.getPolicy();

        double finalReward = policy.play(mdp);
        log.debug("Reward after training {}", finalReward);

        manager.save(dql);

        //useless on toy but good practice!
        mdp.close();
    }
}
