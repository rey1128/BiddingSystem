package com.rey.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BidRequest {
	private String id;
	private Map<String, String> attributes;

	public BidRequest(String id, Map<String, String[]> attributes) {
		super();
		this.id = id;

		this.attributes = new HashMap<>();
		// parameters with same name will be overridden
		for (Entry<String, String[]> entry : attributes.entrySet()) {
			String[] values = entry.getValue();
			this.attributes.put(entry.getKey(), values[values.length - 1]);
		}
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
