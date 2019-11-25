package com.rey.bidding.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.vertx.core.MultiMap;

public class BidRequest {
	private String id;
	private Map<String, String> attributes;

	public BidRequest(String id, Map<String, String> attributes) {
		super();
		this.id = id;
		this.attributes = attributes;
	}

	public BidRequest(String id, MultiMap attributes) {
		super();
		this.id = id;
		this.attributes = attributes.entries().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

}
