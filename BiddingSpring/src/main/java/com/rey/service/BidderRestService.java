package com.rey.service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rey.model.BidReply;
import com.rey.model.BidRequest;

@Service
public class BidderRestService {
	private static final Logger log = LoggerFactory.getLogger(BidderRestService.class);

	private final RestTemplate restTemplate;

	public BidderRestService(RestTemplateBuilder builder) {
		// connection and read timeout is 2 sec each
		this.restTemplate = builder.setConnectTimeout(Duration.ofSeconds(2)).setReadTimeout(Duration.ofSeconds(2))
				.build();
	}

	@Async("restExecutor")
	public CompletableFuture<BidReply> postToBidder(String endpoint, BidRequest bidRequest) {
		log.info("post to bidder: " + endpoint);
		try {
			HttpEntity<BidRequest> request = new HttpEntity<>(bidRequest);
			BidReply reply = this.restTemplate.postForEntity(endpoint, request, BidReply.class).getBody();
			return CompletableFuture.completedFuture(reply);
		} catch (Exception e) {

			log.error(String.format("error with post to %s, error: %s", endpoint, e.getMessage()));
			return CompletableFuture.completedFuture(null);
		}
	}

}
