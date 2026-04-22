package com.example.gateway.api;

import com.example.gateway.dto.LoginRequest;
import com.example.gateway.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.http.client.ClientHttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setup() {
        restTemplate.getRestTemplate().setRequestFactory(
            new org.springframework.http.client.HttpComponentsClientHttpRequestFactory()
        );
    }

    private String loginAs(String username) {
        LoginRequest req = new LoginRequest();
        req.setUsername(username);
        req.setPassword("password");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(req);
        ResponseEntity<LoginResponse> resp = restTemplate.exchange(
            "/api/v1/auth/login", HttpMethod.POST, entity, LoginResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        return resp.getBody().getAccessToken();
    }

    @Test
    void login_positive_returnsToken() {
        String token = loginAs("user");
        assertThat(token).isNotBlank();
    }

    @Test
    void login_negative_wrongPassword_returns401() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user");
        req.setPassword("wrongpassword");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(req);
        ResponseEntity<String> resp = restTemplate.exchange(
            "/api/v1/auth/login", HttpMethod.POST, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void profile_withoutToken_returns401() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/profile", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void profile_withUserToken_returns200() {
        String token = loginAs("user");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> resp = restTemplate.exchange("/api/v1/profile", HttpMethod.GET,
            new HttpEntity<>(headers), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void docs_withUserToken_returns403() {
        String token = loginAs("user");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> resp = restTemplate.exchange("/api/v1/docs", HttpMethod.GET,
            new HttpEntity<>(headers), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void docs_withReaderToken_returns200() {
        String token = loginAs("reader");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> resp = restTemplate.exchange("/api/v1/docs", HttpMethod.GET,
            new HttpEntity<>(headers), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void unauthorized_response_isJson() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/v1/profile", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resp.getHeaders().getContentType().toString()).contains("application/json");
    }
}
