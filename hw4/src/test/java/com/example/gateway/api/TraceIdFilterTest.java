package com.example.gateway.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TraceIdFilterTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void request_generatesTraceIdHeader() {
    ResponseEntity<String> resp = restTemplate.getForEntity("/actuator/health", String.class);
    assertThat(resp.getHeaders().getFirst("X-Trace-Id")).isNotNull();
  }

  @Test
  void request_propagatesTraceIdHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Trace-Id", "custom-trace-123");
    HttpEntity<Void> entity = new HttpEntity<>(headers);
    ResponseEntity<String> resp = restTemplate.exchange(
        "/actuator/health", HttpMethod.GET, entity, String.class);
    assertThat(resp.getHeaders().getFirst("X-Trace-Id")).isEqualTo("custom-trace-123");
  }
}
