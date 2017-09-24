package com.individual.thinking.traitorstown.ai.learning;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Repository
public class LearningRepository {

    private LearningDataManager learningDataManager;
    private final AmazonS3 amazonS3Client;
    private final String bucketName;
    private final boolean learningEnabled;

    @Autowired
    public LearningRepository(TraitorsTownConfiguration configuration, AmazonS3 amazonS3Client){
        this.amazonS3Client = amazonS3Client;
        this.bucketName = configuration.getBucket();
        this.learningEnabled = configuration.getLearningEnabled();
    }

    public void initialize(){
        this.learningDataManager = new LearningDataManager(learningEnabled);
    }

    public void save(Learning<GameState, Integer, DiscreteSpace, IDQN> learning){
        learningDataManager.save(learning);
        File file = learningDataManager.getFile();
        clearS3Bucket();
        amazonS3Client.putObject(new PutObjectRequest(bucketName, UUID.randomUUID().toString(), file));
    }

    public Policy<GameState, Integer> load(){
        File tmp = null;
        try {
            final List<S3ObjectSummary> objectSummaries = amazonS3Client.listObjects(bucketName).getObjectSummaries();

            if (objectSummaries.isEmpty()){
                // looks like there is no training on s3
                return null;
            }

            InputStream inputStream = amazonS3Client.getObject(bucketName, objectSummaries.get(0).getKey()).getObjectContent();
            tmp = File.createTempFile(UUID.randomUUID().toString(), "");
            Files.copy(inputStream, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DQNPolicy<>(learningDataManager.load(tmp));
    }

    public DataManager getDataManager(){
        return learningDataManager;
    }

    private void clearS3Bucket(){
        ObjectListing objects = amazonS3Client.listObjects(bucketName);
        for (S3ObjectSummary object : objects.getObjectSummaries()){
            amazonS3Client.deleteObject(bucketName, object.getKey());
        }
    }
}
