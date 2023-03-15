package com.example.bitter.repository;

import com.example.bitter.entity.Tweet;
import com.example.bitter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByDeletedFalseOrderByPosted();

    List<Tweet> findByDeletedFalseAndHashtags_LabelOrderByPosted(String label);

    List<Tweet> findAllByDeletedFalseAndAuthorInOrderByPosted(List<User> authors);
}
