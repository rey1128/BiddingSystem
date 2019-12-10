package com.rey.BiddingSpring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.rey.service.BidderService;

@SpringBootTest
public class BidderServiceTest {

	@Autowired
	private BidderService bidderSrv;
	
	@Test
	void testName() throws Exception {
		// TODO 
		//bidderSrv.getWinningBidder(endpoints, bidRequest)
	}
	
}
