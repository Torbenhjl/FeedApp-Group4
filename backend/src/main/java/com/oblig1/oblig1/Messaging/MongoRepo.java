package com.oblig1.oblig1.Messaging;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoRepo extends MongoRepository<MongoDocument, String> {
}
