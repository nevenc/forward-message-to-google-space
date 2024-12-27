package com.example.fwd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ForwardMessageToGoogleSpaceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ForwardMessageToGoogleSpaceApplication.class, args);
	}
}

@RestController
@RequestMapping("/webhook")
class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private final RestTemplate restTemplate;
	private final String targetUrl;

	public HomeController(RestTemplate restTemplate, String targetUrl) {
		this.restTemplate = restTemplate;
		this.targetUrl = targetUrl;
	}

	@PostMapping
	public ResponseEntity<String> handleWebhook(@RequestBody Map<String,Object> payload) {

		// Log received payload
		logger.info("Received payload: " + payload);

		// Create the modified payload in text field
		Map<String,Object> forwardingPayload = new HashMap<>();
		forwardingPayload.put("text", payload.toString());

		// Forward the modified payload
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String,Object>> request = new HttpEntity<>(forwardingPayload, headers);
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, request, String.class);
			logger.info("Forwarded payload and received response: " + response.getBody());
			return ResponseEntity.ok("Payload received successfully.");
		} catch (Exception e) {
			logger.error("Error forwarding the payload: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to forward payload: " + e.getMessage());
		}
	}
}

@Configuration
class MyConfiguration {

	@Value("${forward.space}")
	private String space;
	@Value("${forward.key}")
	private String key;
	@Value("${forward.token}")
	private String token;
	@Value("${forward.url-template}")
	private String forwardURLTemplate;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public String targetUrl() {
		return String.format(forwardURLTemplate, space, key, token);
	}
}

