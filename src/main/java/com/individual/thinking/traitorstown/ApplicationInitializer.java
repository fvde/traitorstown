package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.ai.ArtificialIntelligenceService;
import com.individual.thinking.traitorstown.ai.learning.ReinforcementLearningService;
import com.individual.thinking.traitorstown.game.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitializer implements ApplicationRunner {

    private final CardService cardService;
    private final ReinforcementLearningService reinforcementLearningService;
    private final TraitorsTownConfiguration configuration;
    private final ArtificialIntelligenceService artificialIntelligenceService;

    public void run(ApplicationArguments args) {
        cardService.initialize();
        artificialIntelligenceService.initialize();

        if (configuration.getLearningEnabled()){
            reinforcementLearningService.startLearning();
        } else {
            log.info("Learning disabled for this profile!");
        }
    }
}
