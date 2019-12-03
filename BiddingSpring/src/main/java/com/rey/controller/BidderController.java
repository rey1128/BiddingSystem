package com.rey.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rey.model.BidEndpoint;
import com.rey.model.BidReply;
import com.rey.model.BidRequest;
import com.rey.service.BidderService;
import com.rey.service.EndpointService;

@RestController
public class BidderController {

	@Autowired
	private BidderService bidderSrv;
	@Autowired
	private EndpointService endpointSrv;
	private Logger log = LoggerFactory.getLogger(BidderController.class);

	@GetMapping("/")
	public String index() {
		log.info("index");
		return "read the Docs";
	}

	@GetMapping("/api/endpoint")
	public List<BidEndpoint> endpoints() {
		log.info("endpoints");
		return endpointSrv.getEndpoints();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public String getBidding(@PathVariable String id, HttpServletRequest request) {
		log.info("getBidding");
		
		// check bidding parameter
		if (request.getParameterMap().isEmpty()) {
			return "not valid request";
		}

		BidRequest bidRequest = new BidRequest(id, request.getParameterMap());
		BidReply winner = bidderSrv.getWinningBidder(endpointSrv.getEndpoints(), bidRequest);

		// TODO how to set status code
		if (winner == null) {
			return "Bidder Service not available currently";
		} else {
			return winner.toString();
		}
	}

}
