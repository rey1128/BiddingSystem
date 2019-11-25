package com.rey.bidding.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.rey.bidding.commons.CommonConstant;
import com.rey.bidding.model.BidReply;
import com.rey.bidding.model.BidRequest;

import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import rx.Single;

public class BidderService extends AbstractVerticle {
	private Logger log = LoggerFactory.getLogger(BidderService.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		requestBidders();
	}

	public void requestBidders() {
		EventBus eBus = vertx.eventBus();

		eBus.consumer(CommonConstant.BID_BIDDER_ADDRESS, hr -> {
			String bidRequestStr = hr.body().toString();
			List<BidReply> replies = new ArrayList<>();
			Gson gson = new Gson();
			BidRequest bidRequest = gson.fromJson(bidRequestStr, BidRequest.class);

			// get endpoint from EndpointService
			Single<Message<Object>> consumer = eBus.rxRequest(CommonConstant.BID_ENDPOINT_LIST_ADDRESS,
					"request from bidderService");
			consumer.subscribe(succ -> {
				JsonArray endpointsJson = (JsonArray) succ.body();
				List<String> endpoints = IntStream.range(0, endpointsJson.size()).mapToObj(endpointsJson::getString)
						.collect(Collectors.toList());

				// send request to each endpoint
				endpoints.forEach(endpoint -> {
					log.info("send request to endpoint: " + endpoint);
					WebClient wClient = WebClient.create(vertx);
					
					Single<HttpResponse<Buffer>> request = wClient.postAbs(endpoint)
							.putHeader("Content-Type", "application/json").timeout(2000)
							.rxSendBuffer(Buffer.buffer(Json.encode(bidRequest)));
					// reactive handle request
					request.doOnSuccess(res -> {
						log.info("successfully with request to endpoint " + endpoint);
						BidReply reply = gson.fromJson(res.bodyAsString(), BidReply.class);
						replies.add(reply);
					}).doOnError(err -> {
						log.error(String.format("error with request to endpoint: %s, err: %s", endpoint, err));
						replies.add(null);
					}).doAfterTerminate(() -> {
						if (replies.size() >= endpoints.size()) {
							hr.reply(Json.encode(replies));
						}
						wClient.close();
					}).onErrorReturn(throwable -> {
						return null;
					}).subscribe();
				});
			});
		});
	}
}
