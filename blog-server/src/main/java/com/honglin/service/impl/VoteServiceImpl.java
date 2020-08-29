package com.honglin.service.impl;

import com.honglin.dao.VoteRepository;
import com.honglin.entity.Vote;
import com.honglin.service.VoteService;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    public VoteServiceImpl(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public Vote getVoteById(Long id) {
        return voteRepository.findById(id).get();
    }

    @Override
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }

}
