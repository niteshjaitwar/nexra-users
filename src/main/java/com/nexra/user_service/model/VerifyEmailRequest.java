package com.nexra.user_service.model;

import lombok.Data;

@Data
public class VerifyEmailRequest {
    private String email;
    private String otp;
}
