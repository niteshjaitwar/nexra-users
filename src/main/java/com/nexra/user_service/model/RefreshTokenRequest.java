package com.nexra.user_service.model;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
