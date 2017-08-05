package com.individual.thinking.traitorstown.ai.learning.model;

import org.deeplearning4j.rl4j.space.DiscreteSpace;

public class DiscreteActionSpace extends DiscreteSpace {

    private int handSlots;
    private int playerSlots;

    public DiscreteActionSpace(int handSlots, int playerSlots) {
        super(handSlots * playerSlots);
        this.handSlots = handSlots;
        this.playerSlots = playerSlots;
    }

    public Action convert(Integer action){
        return new Action(
                action / handSlots,
                action % handSlots
        );
    }

    public Integer noOp() {
        return -1;
    }
}
