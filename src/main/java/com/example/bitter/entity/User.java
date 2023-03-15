package com.example.bitter.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user_table")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Credentials credentials;

    @CreationTimestamp
    private Timestamp joined;

    private boolean deleted;

    @Embedded
    private Profile profile;

    @OneToMany(mappedBy = "author")
    @EqualsAndHashCode.Exclude
    private List<Tweet> tweets;

    @ManyToMany
    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    @EqualsAndHashCode.Exclude
    private List<Tweet> likes;

    @ManyToMany
    @JoinTable(
            name = "user_mentions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    @EqualsAndHashCode.Exclude
    private List<Tweet> mentions;

    @ManyToMany
    @JoinTable(
            name = "followers_following",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    @EqualsAndHashCode.Exclude
    private List<User> followers;

    @ManyToMany(mappedBy = "followers")
    @EqualsAndHashCode.Exclude
    private List<User> following;

}
