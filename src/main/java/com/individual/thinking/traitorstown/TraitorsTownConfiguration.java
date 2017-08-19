package com.individual.thinking.traitorstown;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("com.individual.thinking.traitorstown")
public class TraitorsTownConfiguration {

    private Boolean learningEnabled;
    private Integer LearningSteps;
    private Boolean messagingEnabled;
    private Integer minimumNumberOfPlayers;
    private Integer maximumNumberOfPlayers;
    private Integer maximumNumberOfCards;

    public TraitorsTownConfiguration() {
    }

    public Boolean getLearningEnabled() {
        return learningEnabled;
    }

    public void setLearningEnabled(Boolean learningEnabled) {
        this.learningEnabled = learningEnabled;
    }

    public Integer getMaximumNumberOfPlayers() {
        return maximumNumberOfPlayers;
    }

    public void setMaximumNumberOfPlayers(Integer maximumNumberOfPlayers) {
        this.maximumNumberOfPlayers = maximumNumberOfPlayers;
    }

    public Integer getMaximumNumberOfCards() {
        return maximumNumberOfCards;
    }

    public void setMaximumNumberOfCards(Integer maximumNumberOfCards) {
        this.maximumNumberOfCards = maximumNumberOfCards;
    }

    public Integer getMinimumNumberOfPlayers() {
        return minimumNumberOfPlayers;
    }

    public void setMinimumNumberOfPlayers(Integer minimumNumberOfPlayers) {
        this.minimumNumberOfPlayers = minimumNumberOfPlayers;
    }

    public Boolean getMessagingEnabled() {
        return messagingEnabled;
    }

    public void setMessagingEnabled(Boolean messagingEnabled) {
        this.messagingEnabled = messagingEnabled;
    }

    public Integer getLearningSteps() {
        return LearningSteps;
    }

    public void setLearningSteps(Integer learningSteps) {
        LearningSteps = learningSteps;
    }
}
