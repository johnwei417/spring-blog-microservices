package com.honglin.service.impl;

import com.honglin.dao.VoteRepository;
import com.honglin.entity.Vote;
import com.honglin.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Override
    public Vote getVoteById(Long id) {
        return voteRepository.findById(id).get();
    }

    @Override
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }

}
