package com.oblig1.oblig1.Messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oblig1.oblig1.Model.VoteMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteMessageListener {

    @Autowired
    private MongoRepo mongoRepo;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    //Specify the queue Rabbit should listen to.
    @RabbitListener(queues = "voteQueue")
    public void receiveVoteMessage(String message) {

        try {
            VoteMessage voteMessage = jacksonObjectMapper.readValue(message, VoteMessage.class);

            MongoDocument mongoDocument = new MongoDocument();
            //Convert the voteMessage into a MongoDocument.
            mongoDocument.setPollId(voteMessage.getPollId());
            mongoDocument.setOptionId(voteMessage.getOptionId());
            mongoDocument.setUserId(voteMessage.getUserId());
            mongoDocument.setVotedAt(voteMessage.getVotedAt());
            mongoDocument.setUpvote(voteMessage.isUpvote());

            // Save to the db
            mongoRepo.save(mongoDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}