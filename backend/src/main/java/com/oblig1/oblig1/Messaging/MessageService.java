package com.oblig1.oblig1.Messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oblig1.oblig1.Model.VoteMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {


    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper jacksonObjectMapper;

    @Autowired
     public MessageService(RabbitTemplate rabbitTemplate, ObjectMapper jacksonObjectMapper) {
         this.rabbitTemplate = rabbitTemplate;
        this.jacksonObjectMapper = jacksonObjectMapper;
    }


    public void sendVoteMessage(VoteMessage voteMessage) {
         try {
             String jsonMessage = jacksonObjectMapper.writeValueAsString(voteMessage);
             rabbitTemplate.convertAndSend("voteQueue", jsonMessage);
         } catch(JsonProcessingException e) {
             e.printStackTrace();
         }

    }
}
