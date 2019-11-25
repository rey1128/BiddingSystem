package com.rey.bidding;

import com.rey.bidding.service.BidderService;
import com.rey.bidding.service.EndpointService;
import com.rey.bidding.service.HttpService;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		// read configurations, env config will override file config
		ConfigStoreOptions fileStore = new ConfigStoreOptions();
		fileStore.setType("file").setConfig(new JsonObject().put("path", "conf/conf.json"));
		ConfigStoreOptions envStore = new ConfigStoreOptions().setType("env");
		
		ConfigRetriever retriever = ConfigRetriever.create(vertx,
				new ConfigRetrieverOptions().addStore(fileStore).addStore(envStore));
		retriever.getConfig(json -> {
			JsonObject config = json.result();
			// deploy EndpointService and BidderService
			Promise<String> dbVerticleDeploymentPromise = Promise.promise();
			vertx.deployVerticle(new EndpointService(), new DeploymentOptions().setConfig(config),
					dbVerticleDeploymentPromise);
			vertx.deployVerticle(BidderService.class.getName(), new DeploymentOptions().setInstances(4).setConfig(config),
					dbVerticleDeploymentPromise);
			dbVerticleDeploymentPromise.future().compose(id -> {
				// when successfully, deply HttpService
				Promise<String> httpVerticleDeploymentPromise = Promise.promise();
				vertx.deployVerticle(HttpService.class.getName(),
						new DeploymentOptions().setInstances(4).setConfig(config), httpVerticleDeploymentPromise);
				return httpVerticleDeploymentPromise.future();
			}).setHandler(ar -> {
				if (ar.succeeded()) {
					startPromise.complete();
				} else {
					startPromise.fail(ar.cause());
				}
			});
		});

	}

}
