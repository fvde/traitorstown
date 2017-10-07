package com.individual.thinking.traitorstown.ai;

import com.individual.thinking.traitorstown.ai.learning.LearningRepository;
import com.individual.thinking.traitorstown.ai.learning.model.Action;
import com.individual.thinking.traitorstown.ai.learning.model.DiscreteActionSpace;
import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import com.individual.thinking.traitorstown.model.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.rl4j.policy.Policy;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile(value = {"development", "learning", "production"})
public class ArtificialIntelligenceService {

    private final DiscreteActionSpace discreteActionSpace;
    private final Policy<GameState, Integer> policy;
    private final Random random = new Random();

    @Autowired
    public ArtificialIntelligenceService(LearningRepository learningRepository, DiscreteActionSpace discreteActionSpace){
        this.discreteActionSpace = discreteActionSpace;
        this.policy = learningRepository.load();
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

    private Action getRecommendedActionForPlayer(GameState state) {
        Integer action = discreteActionSpace.randomAction();
        if (policy != null) {
            try {
                action = policy.nextAction(Nd4j.create(state.toArray()));
            } catch (Exception e){
                log.error("Failed to propose action with exception: {}", e);
            }
        }
        return discreteActionSpace.convert(action);
    }
}
