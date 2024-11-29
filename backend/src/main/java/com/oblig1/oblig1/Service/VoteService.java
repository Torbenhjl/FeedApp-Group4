package com.oblig1.oblig1.Service;

import com.oblig1.oblig1.Model.Poll;
import com.oblig1.oblig1.Model.User;
import com.oblig1.oblig1.Model.Vote;
import com.oblig1.oblig1.Model.VoteOption;
import com.oblig1.oblig1.Repo.VoteOptionRepo;
import com.oblig1.oblig1.Repo.VoteRepo;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    @Autowired
    private VoteRepo voteRepo;

    @Autowired
    private VoteOptionRepo voteOptionRepo;

    // Save a vote
    public Vote saveVote(Vote vote) {
        return voteRepo.save(vote);
    }

    // Check if the user has already voted in the given poll
    public boolean hasUserVoted(Poll poll, String username) {
        return voteRepo.existsByPollAndVotedBy(poll, username);
    }

    public List<Vote> getVotesByPoll(Poll poll) {
        return voteRepo.findByPoll(poll);
    }

    public Integer getVoteCountForOption(Long optionId) {
        VoteOption option = voteOptionRepo.findById(optionId)
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));
        return option.getVoteCount();
    }

    // Count the number of votes for a specific option
    public long countVotesByOption(VoteOption option) {
        return voteRepo.countByOption(option);
    }

    public int getTotalVoteCountByPollId(Long pollId) {
        Poll poll = new Poll();
        poll.setId(pollId);
        return (int) voteRepo.countByPoll(poll); // Casting to int for simplicity, you can use long if needed
    }

    // Count the number of votes in a specific poll
    public long countVotesByPoll(Poll poll) {
        return voteRepo.countByPoll(poll);
    }

    // Retrieve all votes
    public List<Vote> getAllVotes() {
        return voteRepo.findAll();
    }

    // Additional vote-related methods can be added as needed
}
