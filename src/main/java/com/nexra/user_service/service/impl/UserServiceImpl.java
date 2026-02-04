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

        // Setup initial user state, encode password
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(user);

        log.info("UserServiceImpl -> registerUser() Use successfully registered, id = {}", savedUser.getId());
        return modelMapper.map(savedUser, UserDTO.class);
    }

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
}
