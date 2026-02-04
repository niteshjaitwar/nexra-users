package com.nexra.user_service.repository;

import com.nexra.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data Access Object (repository) for the User entity.
 * Extends JpaRepository to inherit standard CRUD operations and defines custom
 * query methods.
 *
 * Use Cases:
 * - Persisting and retrieving User data
 * - Checking existence of users by unique fields
 * - Looking up users for authentication
 *
 * @author niteshjaitwar
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
