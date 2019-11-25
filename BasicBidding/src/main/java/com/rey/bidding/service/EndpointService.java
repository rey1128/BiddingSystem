package com.rey.bidding.service;

import java.util.Arrays;
import java.util.List;

import com.rey.bidding.commons.CommonConstant;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class EndpointService extends AbstractVerticle {
	private Logger log = LoggerFactory.getLogger(EndpointService.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		RedisOptions options = new RedisOptions();
		options.setHost(config().getString(CommonConstant.REDIS_HOST_KEY, "localhost"))
				.setPort(config().getInteger(CommonConstant.REDIS_PORT_KEY, 6379));

		initEndpoints(options);
		listEndpoints(options);

		startPromise.complete();
	}

	private void listEndpoints(RedisOptions options) {
		EventBus eBus = vertx.eventBus();
		eBus.consumer(CommonConstant.BID_ENDPOINT_LIST_ADDRESS, hr -> {
			// get endpoints from redis
			RedisClient.create(vertx, options).smembers(CommonConstant.ENDPOINTS_KEY, res -> {
				if (res.succeeded()) {
					hr.reply(res.result());
				} else {
					log.error("error with redis, return endpoints from configurations. Error: " + res.cause());
					hr.reply(config().getJsonArray("endpoints"));
				}
			});
		});
	}

	private void initEndpoints(RedisOptions options) {
		// clear redis
		RedisClient.create(vertx, options).del(CommonConstant.ENDPOINTS_KEY, del -> {
//			JsonArray endpointsConf = config().getJsonArray(CommonConstant.ENDPOINTS_KEY);
			List<String> endpointsConf = Arrays.asList(config().getString(CommonConstant.ENDPOINTS_KEY).split(","));
			// init redis with configurations
			endpointsConf.forEach(endpoint -> {
				RedisClient.create(vertx, options).sadd(CommonConstant.ENDPOINTS_KEY, endpoint.toString(), hr -> {
					if (hr.succeeded()) {
						log.info(String.format("add endpoint: %s to redis with key: %s", endpoint,
								CommonConstant.ENDPOINTS_KEY));
					} else {
						log.error(String.format("fail to add endpoint %s to redis, error: %s", endpoint, hr.cause()));
					}
				});
			});
		});
	}
}
