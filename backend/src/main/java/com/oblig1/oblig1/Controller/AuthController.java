package com.oblig1.oblig1.Controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:57967", allowCredentials = "true")
@RestController
@RequestMapping("/login/oauth2/code/keycloak")
public class AuthController {

    @GetMapping
    public ResponseEntity<?> handleOAuth2Callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            @RequestParam("session_state") String sessionState,
            @RequestParam("iss") String iss) {
        System.out.println("In the Oauth2 handler");

        // Verify `state` here if necessary (recommended)
        // Optionally validate the `iss` to ensure it matches your expected issuer

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:8080/login/oauth2/code/keycloak"); // Must match Keycloak config
        params.add("client_id", "feedapp-client");
        params.add("client_secret", "your-client-secret"); // Replace with your actual client secret

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            // Exchange code for tokens
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://keycloak:8081/realms/FeedApp/protocol/openid-connect/token",
                    requestEntity,
                    String.class);

            // Parse and return the token response to the frontend
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to exchange token");
        }
    }
}
