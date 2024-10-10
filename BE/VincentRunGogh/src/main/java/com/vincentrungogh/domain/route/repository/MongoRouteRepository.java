package com.vincentrungogh.domain.route.repository;

import com.vincentrungogh.domain.route.entity.MongoRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoRouteRepository extends MongoRepository<MongoRoute, String> {
}
