package com.nexra.user_service.entity;

/**
 * Defines the available roles within the application.
 * These roles are used to control access levels and permissions for users.
 *
 * Use Cases:
 * - Defining authorization authorities
 * - Restricting access to specific endpoints
 *
 * @author niteshjaitwar
 */
public enum Role {
    USER,
    CLIENT,
    STUDENT,
    EMPLOYEE,
    ADMIN,
    SUPER_ADMIN
}
