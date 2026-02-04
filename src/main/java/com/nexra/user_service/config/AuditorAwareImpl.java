package com.nexra.user_service.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of Spring Data's AuditorAware interface.
 * Retrieves the currently authenticated username from the SecurityContext
 * to automatically populate auditing fields (CreatedBy, LastModifiedBy).
 *
 * Use Cases:
 * - Automating audit trails for database changes
 * - Integrating Spring Security with JPA Auditing
 *
 * @author niteshjaitwar
 */
@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM"); // or Optional.empty()
        }

        return Optional.of(authentication.getName());
    }
}
