package com.example.bitter.repository;

import com.example.bitter.entity.Credentials;
import com.example.bitter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByDeletedFalse();

    User findUserByCredentials_Username(String username);

    List<User> findUsersByCredentials_UsernameIn(List<String> usernames);

    Boolean existsByCredentials_Username(String username);

    Boolean existsByCredentials(Credentials credentials);



}
