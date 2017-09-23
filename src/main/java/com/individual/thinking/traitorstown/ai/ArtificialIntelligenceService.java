package com.individual.thinking.traitorstown.ai;

import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.learning.LearningRepository;
import com.individual.thinking.traitorstown.ai.learning.model.Action;
import com.individual.thinking.traitorstown.ai.learning.model.DiscreteActionSpace;
import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import com.individual.thinking.traitorstown.model.Game;
import lombok.RequiredArgsConstructor;
import org.deeplearning4j.rl4j.policy.Policy;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ArtificialIntelligenceService {
    private final DiscreteActionSpace actionSpace;
    private Policy<GameState, Integer> policy;
    private Random random = new Random();

    @Autowired
    public ArtificialIntelligenceService(LearningRepository learningRepository, DiscreteActionSpace actionSpace, TraitorsTownConfiguration traitorsTownConfiguration){
        this.actionSpace = actionSpace;

        if (traitorsTownConfiguration.getLearningEnabled()){
            policy = learningRepository.load();
        }
    }

    public Action getRecommendedAction(Game game, Long player) {
        Action proposal = getRecommendedActionForPlayer(GameState.fromGameAndPlayer(game, player));
        Action finalAction = new Action(
                proposal.getPlayerSlot() < game.getPlayers().size()
                        ? proposal.getPlayerSlot()
                        : random.nextInt(game.getPlayers().size()),
                proposal.getCardSlot() < game.getPlayer(player).getHandCards().size()
                        ? proposal.getCardSlot()
                        : random.nextInt(Math.max(1, game.getPlayer(player).getHandCards().size())));
        return finalAction;
    }

    private Action getRecommendedActionForPlayer(GameState state){
        Integer action =  policy != null
                ? policy.nextAction(Nd4j.create(state.toArray()))
                : actionSpace.randomAction();
        return actionSpace.convert(action);
    }
}
