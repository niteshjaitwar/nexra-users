package com.nexra.user_service.service;

import com.nexra.user_service.model.UserDTO;

import java.util.List;

/**
 * Service Interface defining the business logic contracts for User operations.
 *
 * Use Cases:
 * - Registering new users
 * - Retrieving user details by ID or Username
 * - Updating and Deleting users
 * - Listing all registered users
 *
 * @author niteshjaitwar
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     *
     * @param userDTO the user data containing username, email, password, and roles
     * @return the created UserDTO
     */
    UserDTO registerUser(UserDTO userDTO);

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the user's ID
     * @return the found UserDTO
     */
    UserDTO getUserById(Long id);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return the found UserDTO
     */
    UserDTO getUserByUsername(String username);

    /**
     * Retrieves a list of all registered users.
     *
     * @return a list of UserDTOs
     */
    List<UserDTO> getAllUsers();

    /**
     * Updates an existing user's information.
     *
     * @param id      the ID of the user to update
     * @param userDTO the new user data
     * @return the updated UserDTO
     */
    UserDTO updateUser(Long id, UserDTO userDTO);

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteUser(Long id);

    /**
     * Enables a user account (e.g., after email verification).
     *
     * @param email the user's email
     */
    void enableUser(String email);

    /**
     * Updates a user's password.
     *
     * @param email       the user's email
     * @param newPassword the new password
     */
    void updatePassword(String email, String newPassword);
}
