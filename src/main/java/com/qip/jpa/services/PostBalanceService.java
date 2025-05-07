package com.qip.jpa.services;

import com.qip.jpa.entities.PostBalance;
import com.qip.jpa.repositories.PostBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostBalanceService {

    @Autowired
    private PostBalanceRepository postBalanceRepository;

    public Optional<PostBalance> getPostBalanceById(Long id) {
        return postBalanceRepository.findById(id);
    }


    public PostBalance savePostBalance(PostBalance postBalance) {
        return postBalanceRepository.save(postBalance);
    }

    public void deletePostBalance(Long id) {
        postBalanceRepository.deleteById(id);
    }


}