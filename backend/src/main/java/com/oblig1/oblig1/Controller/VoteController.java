package com.oblig1.oblig1.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

import com.oblig1.oblig1.Messaging.MessageService;
import com.oblig1.oblig1.Model.*;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oblig1.oblig1.Service.PollService;
import com.oblig1.oblig1.Service.VoteService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:57967", allowCredentials = "true")
@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private PollService pollService;

    private final MessageService messageService;

    // Using the RabbitMQ template to send to a queue
    @Autowired
    public VoteController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<Vote> getAllVotes() {
        return voteService.getAllVotes();
    }

    // Submit a new vote
    @PostMapping
    public ResponseEntity<String> submitVote(@RequestBody Map<String, Object> voteData, HttpSession session) {
        Long optionId = ((Number) voteData.get("optionId")).longValue();
        Long pollId = ((Number) voteData.get("pollId")).longValue();
        boolean isUpvote = (boolean) voteData.get("isUpvote"); // Expect this from the frontend
        String username = (String) voteData.get("user"); // Expect this from the frontend
        LocalDateTime now = LocalDateTime.now();

        // Check if poll exists
        Poll poll = pollService.findPollById(pollId);
        if (poll == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Poll not found");
        }
        for (VoteOption option : poll.getVoteOptions()) {
            option.setVoteCount(voteService.getVoteCountForOption(option.getId()));
        }

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username not present");
        }

        VoteOption selectedOption = pollService.findOptionById(optionId);
        if (selectedOption == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vote option not found");
        }

        if (poll.isPrivate() && voteService.hasUserVoted(poll, username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already voted in this poll");
        }

        if (selectedOption.getVoteCount() == null) {
            selectedOption.setVoteCount(0); // Initialize vote count if null
        }

        if (isUpvote) {
            selectedOption.setVoteCount(selectedOption.getVoteCount() + 1);
        } else {
            selectedOption.setVoteCount(selectedOption.getVoteCount() - 1);
        }

        // Save the vote option and vote
        pollService.saveVoteOption(selectedOption);

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setOption(selectedOption);
        vote.setVotedBy(username);
        vote.setVotedAt(LocalDateTime.now());

        voteService.saveVote(vote);

        VoteMessage voteMessage = new VoteMessage();
        voteMessage.setPollId(pollId);
        voteMessage.setOptionId(optionId);
        voteMessage.setUserId(username);
        voteMessage.setVotedAt(LocalDateTime.now());
        voteMessage.setUpvote(isUpvote);

        messageService.sendVoteMessage(voteMessage);

        return ResponseEntity.status(HttpStatus.CREATED).body("Vote submitted successfully");
    }

}
