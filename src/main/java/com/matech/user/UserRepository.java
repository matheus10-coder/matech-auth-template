package com.matech.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Class that is communicating with the database - but we are just extending from JPA interface
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email); // we must fetch user by email
}
