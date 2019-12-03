package com.rey.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rey.model.BidEndpoint;
import com.rey.model.BidReply;
import com.rey.model.BidRequest;

@Service
public class BidderService {
	@Autowired
	private BidderRestService bidderRestSrv;

	public BidReply getWinningBidder(List<BidEndpoint> endpoints, BidRequest bidRequest) {

		BidReply winner = null;

		// send request to each endpoint
		List<CompletableFuture<BidReply>> replyFutures = new ArrayList<>();
		endpoints.stream().forEach(endpoint -> {
			CompletableFuture<BidReply> replyFuture = bidderRestSrv.postToBidder(endpoint.getEndpoint(), bidRequest);
			replyFutures.add(replyFuture);
		});

		try {
			// wait for all requests finished
			List<BidReply> replies = CompletableFuture
					.allOf(replyFutures.toArray(new CompletableFuture[replyFutures.size()]))
					.thenApply(v -> replyFutures.stream().map(future -> future.join()).collect(Collectors.toList()))
					.get();

			// filter and auction
			winner = replies.stream().filter(r -> r != null).max(new Comparator<BidReply>() {
				@Override
				public int compare(BidReply o1, BidReply o2) {
					int compareBid = o1.getBid().compareTo(o2.getBid());
					// descending by bid
					if (compareBid != 0)
						return compareBid;
					// ascending by content
					return o2.getContent().compareTo(o1.getContent());
				}
			}).orElse(null);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return winner;
	}
}
