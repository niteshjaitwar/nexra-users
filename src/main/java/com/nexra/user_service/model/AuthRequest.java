package com.nexra.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Authentication requests.
 * Contains user credentials for login.
 *
 * Use Cases:
 * - User login endpoint payload
 *
 * @author niteshjaitwar
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}
