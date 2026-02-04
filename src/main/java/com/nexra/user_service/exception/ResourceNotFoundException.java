package com.nexra.user_service.exception;

/**
 * Exception thrown when a requested resource is not found in the database.
 *
 * Use Cases:
 * - User lookup by ID fails
 * - User lookup by username fails
 *
 * @author niteshjaitwar
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
