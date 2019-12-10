package com.rey.bidding.service;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.rey.bidding.MainVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;

@RunWith(VertxUnitRunner.class)
public class HttpServiceTest {
	private Vertx vertx;

	@Before
	public void setUp(TestContext context) {
		vertx = Vertx.vertx();
		vertx.deployVerticle(MainVerticle.class.getName(), context.asyncAssertSuccess());
	}

	@Test
	public void testDefaultAPI(TestContext context) throws Exception {

		// TODO ADD TIMEOUT ASSERTION
		Async async = context.async();
		WebClient client = WebClient.create(vertx);

		client.getAbs("http://localhost:8080").send(hr -> {
			context.assertEquals("read the docs!", hr.result().body().toString());
			context.assertEquals(200, hr.result().statusCode());
			async.complete();
		});
	}

	@Test
	public void testGetEndpoints_EndpointServiceNotAvailable(TestContext context) throws Exception {
		// stop endpoint service
		vertx.undeploy(EndpointService.class.getName(), ar -> {
			if (ar.succeeded()) {
				// touch endpoint service
				Async async = context.async();
				WebClient client = WebClient.create(vertx);
				client.getAbs("http://localhost:8080/api/endpoint").send(hr -> {
					context.assertEquals(500, hr.result().statusCode());
					context.assertEquals("Internal Error.", hr.result().body().toString());
					async.complete();
				});
			}
		});

	}

	@SuppressWarnings("unchecked")
	private Set<String> convertJsonArrayToSet(JsonArray jsonArray) {
		Set<String> set = new HashSet<>();
		set.addAll(jsonArray.getList());
		return set;
	}

	@Test
	public void testGetEndpoints(TestContext context) throws Exception {
		JsonArray expected = new JsonArray(
				"[\"http://localhost:8081\",\"http://localhost:8082\",\"http://localhost:8083\"]");

		Async async = context.async();
		WebClient client = WebClient.create(vertx);
		client.getAbs("http://localhost:8080/api/endpoint").send(hr -> {
			context.assertEquals(200, hr.result().statusCode());
			assertEquals(convertJsonArrayToSet(expected), convertJsonArrayToSet(hr.result().bodyAsJsonArray()));
			async.complete();
		});
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

}
