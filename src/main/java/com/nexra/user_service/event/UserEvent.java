package com.nexra.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private String eventType; // e.g., REGISTRATION, FORGOT_PASSWORD
    private String email;
    private String payload; // e.g., OTP or Username
}
