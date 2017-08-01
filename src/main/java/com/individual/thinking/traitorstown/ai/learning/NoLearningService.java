package com.individual.thinking.traitorstown.ai.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"development", "production"})
public class NoLearningService implements LearningService{
    @Override
    public void startLearning() {
        log.info("Learning disabled for this profile!");
    }
}
