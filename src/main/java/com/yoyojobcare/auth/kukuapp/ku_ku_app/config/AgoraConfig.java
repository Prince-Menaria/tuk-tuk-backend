package com.yoyojobcare.auth.kukuapp.ku_ku_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class AgoraConfig {

    @Value("${agora.appId}")
    private String appId;

    @Value("${agora.appCertificate}")
    private String appCertificate;

    @Value("${agora.tokenExpiry:86400}")
    private int tokenExpiry;
}
