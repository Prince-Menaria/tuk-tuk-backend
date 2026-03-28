package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import java.util.Map;

public interface AuthService {

    public Map<String, Object> refreshTokens(String refreshToken);

}
