package com.rey.BiddingSpring;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.rey.model.BidEndpoint;
import com.rey.repository.EndpointRepository;
import com.rey.service.EndpointService;

@SpringBootTest
public class EndpointServiceTest {

	@Autowired
	private EndpointService endpointSrv;
	@MockBean
	private EndpointRepository endpointRepo;
	@Value("${endpoints: }")
	private String endpointsConf;

	private String defaultEndpoint = "http://localhost:8081";

	@Test
	public void whenRedis_getEndpointsShouldReturn() {
		// set up mock
		List<BidEndpoint> defaultEndpoints = new ArrayList<>();
		defaultEndpoints.add(new BidEndpoint(defaultEndpoint, defaultEndpoint));
		Mockito.when(endpointRepo.findAll()).thenReturn(defaultEndpoints);

		List<BidEndpoint> endpoints = endpointSrv.getEndpoints();
		assertEquals(1, endpoints.size());
		assertEquals(defaultEndpoint, endpoints.get(0).getEndpoint());
	}

	@Test
	public void whenConf_getEndpointsShouldReturn() {
		assertThrows(Exception.class, () -> {
			Mockito.doThrow(new Exception("redis is not available")).when(endpointRepo).findAll();
		}, "redis is not available");

		List<BidEndpoint> endpoints = endpointSrv.getEndpoints();
		assertEquals(endpointsConf.split(",").length, endpoints.size());
		endpoints.forEach(endpoint -> {
			assertTrue(endpointsConf.contains(endpoint.getEndpoint()));
		});

	}
}
