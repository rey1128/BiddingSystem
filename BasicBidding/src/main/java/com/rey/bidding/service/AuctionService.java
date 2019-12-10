package com.rey.bidding.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.rey.bidding.model.BidReply;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class AuctionService {

	private static Logger log = LoggerFactory.getLogger(AuctionService.class);

	private static final Comparator<BidReply> ascendingComparator = new Comparator<BidReply>() {
		@Override
		public int compare(BidReply o1, BidReply o2) {
			int compareBid = o1.getBid().compareTo(o2.getBid());
			// by bid
			if (compareBid != 0)
				return compareBid;
			// by content
			return o2.getContent().compareTo(o1.getContent());
		}
	};

	private static final Comparator<BidReply> descendingComparator = new Comparator<BidReply>() {
		@Override
		public int compare(BidReply o1, BidReply o2) {
			int compareBid = o2.getBid().compareTo(o1.getBid());
			// descending by bid
			if (compareBid != 0)
				return compareBid;
			// ascending by content
			return o1.getContent().compareTo(o2.getContent());
		}
	};

	// get winner with own price
	BidReply getWinner(List<BidReply> replies) {
		log.info("get winner with original price");
		return replies.stream().filter(r -> r != null).max(ascendingComparator).orElse(null);
	}

	// get winner's price should be price of second winner+1
	static BidReply getWinnerWithSecondPrice(List<BidReply> replies) {
		log.info("get winner with second price");

		BidReply winnerReply = null;
		Long finalBid;

		List<BidReply> sortedReplies = replies.stream().filter(r -> r != null).sorted(descendingComparator)
				.collect(Collectors.toList());

		if (!sortedReplies.isEmpty()) {
			BidReply max = sortedReplies.get(0);
			finalBid = max.getBid();

			// if there are more than 1 bidder, compute the final bid for the winner
			if (sortedReplies.size() >= 1) {
				log.info("compute the final bid for the winner");
				BidReply second = sortedReplies.get(1);
				finalBid = max.getBid().equals(second.getBid()) ? max.getBid() : (second.getBid() + 1);
			}
			log.info("final bid price is " + finalBid);
			winnerReply = new BidReply(max.getId(), finalBid, max.getContent());
		}

		return winnerReply;
	}
}
