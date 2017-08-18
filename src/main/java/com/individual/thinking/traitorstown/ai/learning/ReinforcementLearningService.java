package com.individual.thinking.traitorstown.ai.learning;

import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.learning.model.Action;
import com.individual.thinking.traitorstown.ai.learning.model.DiscreteActionSpace;
import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import com.individual.thinking.traitorstown.ai.learning.model.TraitorsTownMDP;
import com.individual.thinking.traitorstown.game.GameService;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.user.UserService;
import lombok.RequiredArgsConstructor;
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
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReinforcementLearningService {

    private QLearning.QLConfiguration TRAITORS_QL =
            new QLearning.QLConfiguration(
                    123,   //Random seed
                    100000,//Max step By epoch
                    80000, //Max step default 80000
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


    private AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration TRAITORS_ASYNC_QL =
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

    private A3CDiscrete.A3CConfiguration TRAITORS_A3C =
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

    private DQNFactoryStdDense.Configuration TRAITORS_NET =
            DQNFactoryStdDense.Configuration.builder()
                    .l2(0.01).learningRate(1e-2).numLayer(3).numHiddenNodes(16).build();

    private final UserService userService;
    private final GameService gameService;
    private final RewardService rewardService;
    private final DiscreteActionSpace actionSpace;
    private final TraitorsTownConfiguration configuration;

    private final LearningRepository learningRepository;
    private List<Player> players = new ArrayList<>();
    private TraitorsTownMDP mdp;


    public void startLearning(){
        try {
            prepareSession();
        } catch (Exception e) {
            log.error("Failed to prepare learning session with exception {}", e);
        }
        trainAI();
        testLearningQuality();
    }

    private void prepareSession() throws Exception {
        players.add(userService.register("learner","learner", false).getPlayer());
        while (players.size() < configuration.getMaximumNumberOfPlayers()){
            players.add(userService.register("ai" + players.size(),"ai" + players.size(), true).getPlayer());
        }

        mdp = new TraitorsTownMDP(
                gameService,
                rewardService,
                players.stream().map(p -> p.getId()).collect(Collectors.toList()),
                actionSpace,
                configuration);
    }

    private void trainAI() {
        //define the training method
        Learning<GameState, Integer, DiscreteSpace, IDQN> dql = new QLearningDiscreteDense<>(mdp, TRAITORS_NET, TRAITORS_QL, learningRepository.getDataManager());

        // TODO try other algorithm

        //start the training
        dql.train();

        learningRepository.save(dql);
    }

    public void testLearningQuality(){
        // load again
        Policy<GameState, Integer> learned = learningRepository.load();

        for (int x = 1; x <= 10; x++){
            GameState state = mdp.reset();
            Action recommendedAction = mdp.getActionSpace().convert(learned.nextAction(Nd4j.create(state.toArray())));
            log.info("Scenario {}: Suggested action {}:{} for game {}",
                    x,
                    recommendedAction,
                    mdp.getReadable(recommendedAction),
                    state);
        }
    }
}
