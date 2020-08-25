package com.honglin.service;

import com.honglin.entity.Vote;

public interface VoteService {
    /**
     * get Vote by id
     *
     * @param id
     * @return
     */
    Vote getVoteById(Long id);

    /**
     * delte Vote
     *
     * @param id
     * @return
     */
    void removeVote(Long id);
}
