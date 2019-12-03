package com.rey.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("endpoint")
public class BidEndpoint {
	@Id
	private String endpoint;
	private String name;

	public BidEndpoint(String endpoint, String name) {
		super();
		this.endpoint = endpoint;
		this.name = name;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.endpoint;
	}
}
