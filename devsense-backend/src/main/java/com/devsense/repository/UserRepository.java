package com.devsense.repository;

import com.devsense.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // JpaRepository<User, UUID> gives you for free:
    // save(), findById(), findAll(), deleteById(), count(), existsById() etc.

    Optional<User> findByEmail(String email);
    // Spring generates: SELECT * FROM users WHERE email = ? automatically
    // Optional<> means it might not exist — forces caller to handle empty case

    boolean existsByEmail(String email);
    // Spring generates: SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)
    // Used in registration to check if email is already taken
}

