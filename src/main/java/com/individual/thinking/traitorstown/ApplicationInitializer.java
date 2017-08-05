package com.individual.thinking.traitorstown;

import com.individual.thinking.traitorstown.ai.ArtificialIntelligenceService;
import com.individual.thinking.traitorstown.ai.learning.LearningService;
import com.individual.thinking.traitorstown.game.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationInitializer implements ApplicationRunner {

    private final CardService cardService;
    private final LearningService learningService;
    private final ArtificialIntelligenceService artificialIntelligenceService;

    public void run(ApplicationArguments args) {
        cardService.initializeCards();
        learningService.startLearning();
        artificialIntelligenceService.setup();
    }
}
