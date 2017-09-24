package com.individual.thinking.traitorstown.ai.learning;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.util.Constants;
import org.deeplearning4j.rl4j.util.DataManager;

import java.io.File;

public class LearningDataManager extends DataManager{
    private static final String LEARNING_NAME = "traitorstown.training";
    public static final String ROOT_LEARNING_DIR = "learning";

    public LearningDataManager(Boolean saveData){
        super(ROOT_LEARNING_DIR, saveData);
    }

    public void save(Learning learning){
        save(getModelDir() + "/" + LEARNING_NAME, learning);
    }

    public IDQN load(File file ) {
        return load(file, QLearning.QLConfiguration.class).getFirst();
    }

    public File getFile() {
        return new File(ROOT_LEARNING_DIR + "/" + findIndexOfLatestLearningDirectory() +  "/" + Constants.MODEL_DIR + "/" + LEARNING_NAME);
    }

    private Integer findIndexOfLatestLearningDirectory(){
        File dr = new File(ROOT_LEARNING_DIR);
        File[] rootChildren = dr.listFiles();

        int i = 0;
        while (hasSubDirectoryOfName(rootChildren, i + 1 + ""))
            i++;

        return i;
    }

    private boolean hasSubDirectoryOfName(File[] files, String name) {
        boolean exists = false;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(name)) {
                exists = true;
                break;
            }
        }
        return exists;
    }
}
