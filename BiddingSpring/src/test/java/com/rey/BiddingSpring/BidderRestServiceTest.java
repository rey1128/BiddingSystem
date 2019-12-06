package com.rey.BiddingSpring;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.rey.model.BidReply;
import com.rey.model.BidRequest;
import com.rey.service.BidderRestService;

@SpringBootTest
public class BidderRestServiceTest {
	@Autowired
	private BidderRestService bidderRestSrv;
	@Mock
	private RestTemplate mockTmp;
	private String someEndpoint;
	private String someId;
	private String someBidder;
	private Map<String, String[]> attributes;
	private BidRequest request;
	private Long someBid;
	private String someContent;
	private BidReply reply;

	@BeforeEach
	public void setup() {
		this.someEndpoint = "someEndpoint";
		this.someId = "mytest";
		this.someBidder = "a";
		this.attributes = new HashMap<>();
		this.attributes.put(someBidder, new String[] { "100" });
		this.request = new BidRequest(someId, attributes);
		this.someBid = 1000l;
		this.someContent = someBidder + ":$price$" + someBid;
		this.reply = new BidReply(someId, someBid, someContent);
	}

	@Test
	public void testPostToEndpoint_withUnavailableEndpoint() throws Exception {
		Mockito.when(mockTmp.postForEntity(someEndpoint, request, BidReply.class)).thenReturn(null);

		assertTimeout(Duration.ofSeconds(2), () -> {
			CompletableFuture<BidReply> relpyFuture = bidderRestSrv.postToBidder(someEndpoint, request);
			assertNull(relpyFuture.get());
		});
	}

	@Test
	void testPostToEndpoint() throws Exception {
		Mockito.when(mockTmp.postForEntity(someEndpoint, request, BidReply.class))
				.thenReturn(new ResponseEntity<BidReply>(reply, HttpStatus.OK));
		
		assertTimeout(Duration.ofSeconds(2), () -> {
			CompletableFuture<BidReply> relpyFuture = bidderRestSrv.postToBidder(someEndpoint, request);
			relpyFuture.thenAccept(reply->{
				assertEquals(someBid, reply.getBid());
				assertEquals(someId, reply.getId());
				assertEquals(someContent, reply.getContent());
				assertEquals(someBidder + ":" + someBid, reply.toString());	
			});
		});
	}

}
