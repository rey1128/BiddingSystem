package com.rey.repository;

import org.springframework.data.repository.CrudRepository;

import com.rey.model.BidEndpoint;

public interface EndpointRepository extends CrudRepository<BidEndpoint, String> {

}
