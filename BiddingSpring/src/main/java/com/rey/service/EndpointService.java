package com.rey.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rey.model.BidEndpoint;
import com.rey.repository.EndpointRepository;

@Service
public class EndpointService {

	@Value("${endpoints: }")
	private String endpointsConf;
	@Autowired
	private EndpointRepository endpointRepo;
	private Logger log = LoggerFactory.getLogger(EndpointService.class);

	public void saveEndpoints(List<BidEndpoint> endpoints) {
		endpointRepo.saveAll(endpoints);
	}

	public void resetEndpoints() {
		endpointRepo.deleteAll();
		log.info("endpoints from redis are cleared");
		saveEndpoints(readEndpointFromConf());
		log.info("endpoints from configuration are reloaded into redis");
	}

	private List<BidEndpoint> readEndpointFromConf() {
		log.info("endpoints from property file: " + endpointsConf);
		List<BidEndpoint> endpointsFromConf = new ArrayList<>();
		Arrays.asList(endpointsConf.split(",")).forEach(endpoint -> {
			endpointsFromConf.add(new BidEndpoint(endpoint, endpoint));
		});

		return endpointsFromConf;
	}

	public List<BidEndpoint> getEndpoints() {
		try {
			List<BidEndpoint> endpoints = new ArrayList<>();
			endpointRepo.findAll().forEach(endpoints::add);
			log.info("endpoints from redis: "+endpoints);
			return endpoints;
		} catch (Exception e) {
			log.error("error with querying with redis, return endpoints from configuration instead, error: "
					+ e.getMessage());
			return readEndpointFromConf();
		}
	}
}
