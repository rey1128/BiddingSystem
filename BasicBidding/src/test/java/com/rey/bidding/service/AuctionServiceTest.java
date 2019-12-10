package com.rey.bidding.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import com.rey.bidding.model.BidReply;

public class AuctionServiceTest {
	@Test
	public void testGetWinnerWithSecondPrice_EmptyReplyList() throws Exception {
		BidReply reply = AuctionService.getWinnerWithSecondPrice(new ArrayList<BidReply>());
		assertNull(reply);
	}
	
	@Test
	public void testGetWinnerWithSecondPrice_NullReplyList() throws Exception {
		List<BidReply> replies=new ArrayList<BidReply>();
		IntStream.range(1, 5).forEach(i->replies.add(null));
		BidReply reply = AuctionService.getWinnerWithSecondPrice(replies);
		assertNull(reply);
	}

	@Test
	public void testGetWinnerWithSecondPrice_SecondPrice() throws Exception {
		List<BidReply> replies = new ArrayList<>();
		BidReply replyMax = new BidReply("b", 100l, "b:$price$");
		BidReply replySecond = new BidReply("a", 5l, "a:$price$");
		replies.add(replyMax);
		replies.add(replySecond);

		assertEquals(6l, AuctionService.getWinnerWithSecondPrice(replies).getBid().longValue());
	}

	@Test
	public void testGetWinnerWithSecondPrice_SameMaxPrice() throws Exception {
		List<BidReply> replies = new ArrayList<>();
		BidReply replyMax = new BidReply("b", 100l, "b:$price$");
		BidReply replySecond = new BidReply("a", 100l, "a:$price$");
		replies.add(replyMax);
		replies.add(replySecond);

		BidReply winner = AuctionService.getWinnerWithSecondPrice(replies);
		assertEquals(100l, winner.getBid().longValue());
		assertEquals("a", winner.getId());
	}

}
