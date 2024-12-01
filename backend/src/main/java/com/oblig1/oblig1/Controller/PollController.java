package com.oblig1.oblig1.Controller;

import com.oblig1.oblig1.Model.Poll;
import com.oblig1.oblig1.Model.Vote;
import com.oblig1.oblig1.Model.VoteOption;
import com.oblig1.oblig1.Service.PollService;
import com.oblig1.oblig1.Service.VoteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/api/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @Autowired
    private VoteService voteService;

    @GetMapping
    public List<Poll> getAllPolls() {
        List<Poll> polls = pollService.getAllPolls();
        for (Poll poll : polls) {
            // Calculate total votes from VoteRepo for this poll
            long totalVotes = voteService.countVotesByPoll(poll);
            poll.setTotalVotes(totalVotes); // Ensure Poll entity has a totalVotes attribute
            for (VoteOption option : poll.getVoteOptions()) {
                option.setVoteCount(voteService.getVoteCountForOption(option.getId()));
            }
        }
        return polls;
    }
    
    

    // Get a single poll by its ID
// In PollController.java
@GetMapping("/{pollId}")
public ResponseEntity<Poll> getPollById(@PathVariable Long pollId) {
    Optional<Poll> optPoll = pollService.getPollById(pollId);
    if (optPoll.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    Poll poll = optPoll.get();
    int totalVotes = voteService.getTotalVoteCountByPollId(pollId); // Get total votes for the poll
    poll.setTotalVotes(totalVotes); // Assuming you added a totalVotes field in Poll
    return ResponseEntity.ok(poll);
}


    // Create a new poll
    @PostMapping
public ResponseEntity<String> createPoll(@RequestBody Map<String, Object> pollData, HttpSession session) {
    try {
        System.out.println("PollData " + pollData);
        // Extract poll details
        String question = (String) pollData.get("question");
        boolean isPrivate = (boolean) pollData.get("isPrivate");
        String username = (String) pollData.get("user");
     //   String createdBy = request.getUserPrincipal().getName(); // Get username from Keycloak token
        LocalDateTime validUntil = LocalDateTime.parse((String) pollData.get("validUntil"));
        List<Map<String, Object>> optionsData = (List<Map<String, Object>>) pollData.get("voteOptions");

        // Validate inputs
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Poll question is required");
        }
        if (optionsData == null || optionsData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Poll must have at least one vote option");
        }

        System.out.println("Usernae when creating poll:" + username);
        // Create and populate Poll entity
        Poll poll = new Poll();
        poll.setQuestion(question);
        poll.setPrivate(isPrivate);
        poll.setCreatedBy(username);
        poll.setValidUntil(validUntil);
        poll.setPublishedAt(LocalDateTime.now());

        // Process vote options
        List<VoteOption> options = new ArrayList<>();
        int order = 1;
        for (Map<String, Object> optionData : optionsData) {
            String caption = (String) optionData.get("caption");
            if (caption == null || caption.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Each vote option must have a caption");
            }
            VoteOption option = new VoteOption();
            option.setCaption(caption);
            option.setPresentationOrder(order++);
            option.setPoll(poll);
            options.add(option);
        }
        poll.setVoteOptions(options);

        // Save Poll and associated VoteOptions
        pollService.savePoll(poll);

        // Return success response
        return ResponseEntity.status(HttpStatus.CREATED).body("Poll created successfully");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the poll");
    }
}

    

    

    // Update an existing poll
    @PutMapping("/{pollId}")
    public ResponseEntity<Poll> updatePoll(@PathVariable Long pollId, @RequestBody Poll pollDetails) {
        Poll existingPoll = pollService.findPollById(pollId);
        if (existingPoll == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Update poll details, including validUntil and isPrivate
        existingPoll.setQuestion(pollDetails.getQuestion());
        existingPoll.setValidUntil(pollDetails.getValidUntil()); // Update the validUntil timestamp
        existingPoll.setPrivate(pollDetails.isPrivate());   // Update the isPrivate flag

        Poll updatedPoll = pollService.savePoll(existingPoll);
        return ResponseEntity.ok(updatedPoll);
    }

    // Delete a poll
    @DeleteMapping("/{pollId}")
    public ResponseEntity<String> deletePoll(@PathVariable("pollId") Long pollId) {
        Optional<Poll> optPoll = pollService.getPollById(pollId);
        if (optPoll.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Poll not found");
        }

        pollService.deletePoll(pollId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Poll deleted successfully");
    }

    // Get votes for a poll
    @GetMapping("/polls/{pollId}/votes")
    public List<Vote> getVotesForPoll(@PathVariable Long pollId) {
        Poll poll = pollService.findPollById(pollId);
        return voteService.getVotesByPoll(poll);
    }
}
