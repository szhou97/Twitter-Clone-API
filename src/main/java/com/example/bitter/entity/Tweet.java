package com.example.bitter.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
public class Tweet {
    @Id
    @GeneratedValue
    private Long id;

    private Timestamp posted = Timestamp.valueOf(LocalDateTime.now()); // must be generated upon creation

    private boolean deleted;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "in_reply_to_id")
    private Tweet inReplyTo;

    @OneToMany(mappedBy = "inReplyTo")
    private List<Tweet> replies;

    @ManyToOne
    @JoinColumn(name = "repost_of_id")
    private Tweet repostOf;

    @OneToMany(mappedBy = "repostOf")
    private List<Tweet> reposts;

    @ManyToMany
    @JoinTable(
            name = "tweet_hashtags",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
    private Set<Hashtag> hashtags;

    @ManyToMany(mappedBy = "likes")
    private Set<User> likedBy;

    @ManyToMany(mappedBy = "mentions")
    private Set<User> mentioned;
}
