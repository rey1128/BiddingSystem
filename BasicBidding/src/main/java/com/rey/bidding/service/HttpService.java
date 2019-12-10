package com.rey.bidding.service;

import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.rey.bidding.commons.CommonConstant;
import com.rey.bidding.model.BidReply;
import com.rey.bidding.model.BidRequest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpService extends AbstractVerticle {
	private Logger log = LoggerFactory.getLogger(HttpService.class);
	TypeToken<List<BidReply>> BID_REPLY_LIST = new TypeToken<List<BidReply>>() {
		private static final long serialVersionUID = -6450375827775578331L;
	};

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		router.get("/api/endpoint").handler(this::endpointHandler);
		router.get("/:id").handler(this::bidHandler);
		router.get("/*").handler(this::index);

		// start http server
		int port = config().getInteger(CommonConstant.HTTP_SERVER_PORT_KEY, 8080);
		server.requestHandler(router).listen(port, ar -> {
			if (ar.succeeded()) {
				log.info("http server listening at " + port);
				startPromise.complete();
			} else {
				log.error("Error with starting http server, " + ar.cause());
				startPromise.fail(ar.cause());
			}
		});
	}

	private void bidHandler(RoutingContext context) {
		log.info("bid request");
		// get params from http request
		String id = context.request().getParam("id");
		MultiMap queryParams = context.queryParams();
		if (queryParams.isEmpty()) {
			context.response().setStatusCode(400).end("invalid request");
			return;
		}

		BidRequest request = new BidRequest(id, queryParams);
		EventBus eBus = vertx.eventBus();
		DeliveryOptions options = new DeliveryOptions().setSendTimeout(4000);
		// send to bidder service
		eBus.request(CommonConstant.BID_BIDDER_ADDRESS, Json.encode(request), options, rh -> {
			if (rh.succeeded()) {
				// auction
				Gson gson = new Gson();
				List<BidReply> replies = gson.fromJson(rh.result().body().toString(), BID_REPLY_LIST.getType());
				
				BidReply winnderReply=AuctionService.getWinnerWithSecondPrice(replies);
				// return auction result
				if (winnderReply != null) {
					context.response().end(winnderReply.toString());
				} else {
					context.response().setStatusCode(503).end("Bidder Service not available currently");
				}
			} else {
				log.error("error with bid request, error: " + rh.cause());
				context.response().setStatusCode(500).end("Internal Error.");
			}
		});
	}
	


	private void endpointHandler(RoutingContext context) {
		log.info("list endpoints");
		String requestClient = context.request().remoteAddress().toString();
		vertx.eventBus().request(CommonConstant.BID_ENDPOINT_LIST_ADDRESS, requestClient, hr -> {
			if (hr.succeeded()) {
				String info = hr.result().body() == null ? "No available endpoints" : hr.result().body().toString();
				context.response().end(info);
			} else {
				context.response().setStatusCode(500).end("Internal Error.");
			}
		});
	}

	private void index(RoutingContext context) {
		log.info("index");
		context.response().end("read the docs!");
	}
}
