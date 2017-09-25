package com.individual.thinking.traitorstown.ai.learning;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.individual.thinking.traitorstown.TraitorsTownConfiguration;
import com.individual.thinking.traitorstown.ai.learning.model.GameState;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Repository
public class LearningRepository {

    private final DataManager dataManager;
    private final AmazonS3 amazonS3Client;
    private final String bucketName;

    @Autowired
    public LearningRepository(TraitorsTownConfiguration configuration, AmazonS3 amazonS3Client){
        this.amazonS3Client = amazonS3Client;
        this.bucketName = configuration.getBucket();
        this.dataManager = new DataManager(false);
    }

    public void save(Learning<GameState, Integer, DiscreteSpace, IDQN> learning){
        clearS3Bucket();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataManager.save(outStream, learning);
        amazonS3Client.putObject(new PutObjectRequest(bucketName, UUID.randomUUID().toString(), new ByteArrayInputStream(outStream.toByteArray()), new ObjectMetadata()));
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

        return new DQNPolicy<>(dataManager.load(tmp, QLearning.QLConfiguration.class).getFirst());
    }

    private void clearS3Bucket(){
        ObjectListing objects = amazonS3Client.listObjects(bucketName);
        for (S3ObjectSummary object : objects.getObjectSummaries()){
            amazonS3Client.deleteObject(bucketName, object.getKey());
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
