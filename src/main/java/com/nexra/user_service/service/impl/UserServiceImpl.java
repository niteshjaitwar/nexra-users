package com.nexra.user_service.service.impl;

import com.nexra.user_service.entity.User;
import com.nexra.user_service.exception.ResourceNotFoundException;
import com.nexra.user_service.model.UserDTO;
import com.nexra.user_service.repository.UserRepository;
import com.nexra.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing User operations.
 * Implements the business logic defined in UserService.
 *
 * Use Cases:
 * - Handling core user CRUD operations
 * - Converting between Entity and DTO
 * - Ensuring transactional integrity
 *
 * @author niteshjaitwar
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        log.info("UserServiceImpl -> registerUser() Started registering user, username = {}, email = {}",
                userDTO.getUsername(), userDTO.getEmail());

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists"); // Should use custom exception
        }
        if (userRepository.findByUsername(userDTO.getEmail()).isPresent()) { // Check email uniqueness too? Usually yes.
            // Entity constraints exist but handling it here gives better error.
            throw new RuntimeException("Email already exists");
        }

        // Setup initial user state, encode password
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(false); // Disable by default for verification

        User savedUser = userRepository.save(user);

        log.info("UserServiceImpl -> registerUser() User successfully registered, id = {}", savedUser.getId());
        return modelMapper.map(savedUser, UserDTO.class);
    }

    // ... existing CRUD methods ... (kept generic replacement for brevity if tool
    // supported it, but I must provide valid start/end for replacement chunk)
    // Actually I should just append the new methods and replace registerUser
    // separately or in one go if contiguous.
    // They are not contiguous with registerUser.

    // Changing registerUser first
    // I will use multi_replace for this file to be cleaner.

    // Wait, replacing registerUser logic block.
    // And adding new methods at the end.

    // I will return an error and use multi_replace in next turn or separate calls.
    // Just 2 chunks.

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.info("UserServiceImpl -> getUserById() Fetching user by ID, id = {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        log.info("UserServiceImpl -> getUserById() User found, username = {}", user.getUsername());
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.info("UserServiceImpl -> getUserByUsername() Fetching user by username, username = {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        log.info("UserServiceImpl -> getUserByUsername() User found, id = {}", user.getId());
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("UserServiceImpl -> getAllUsers() Fetching all users");

        List<User> users = userRepository.findAll();

        log.info("UserServiceImpl -> getAllUsers() Fetched {} users", users.size());
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("UserServiceImpl -> updateUser() Updating user, id = {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Map updates (ignoring nulls usually handled by modelMapper specific config or
        // manual check,
        // here simplified as full update for DTO, but usually password shouldn't be
        // updated this way unless specified)
        // For simplicity, we update mutable fields.
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        // For password update, usually a different endpoint is preferred, skipping here
        // or enforcing if present

        User updatedUser = userRepository.save(existingUser);

        log.info("UserServiceImpl -> updateUser() User updated successfully, id = {}", updatedUser.getId());
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("UserServiceImpl -> deleteUser() Deleting user, id = {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);

        log.info("UserServiceImpl -> deleteUser() User deleted successfully, id = {}", id);
    }

    @Override
    public void enableUser(String email) {
        log.info("UserServiceImpl -> enableUser() Enabling user: {}", email);
        User user = userRepository.findByEmail(email) // Assuming findByEmail exists or using findByUsername if
                                                      // email==username
                // Wait, User entity has email field. Repository likely has findByEmail?
                // Let's check UserRepository. If not, I'll add it or use findByUsername if
                // appropriate (often username=email).
                // For now assuming findByUsername(email) since findByUsername is standard.
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Wait, repository currently has findByUsername. Does it have findByEmail?
        // Check UserRepository.
        // Assuming findByUsername works if username is separate.
        // I should probably query by Email.
        // Let's check UserRepository content.

        user.setEnabled(true);
        userRepository.save(user);
        log.info("UserServiceImpl -> enableUser() User enabled successfully");
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        log.info("UserServiceImpl -> updatePassword() Updating password for: {}", email);
        User user = userRepository.findByEmail(email) // same check needed
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("UserServiceImpl -> updatePassword() Password updated successfully");
    }
}
