package com.techbank.account.cmd.peter.repository;

import com.techbank.account.common.events.EventModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventStoreRepository extends MongoRepository<EventModel, String> {
    List<EventModel> findByAggregateIdentifierOrderByVersionAsc(String aggregateIdentifier);

    Optional<EventModel> findFirstByAggregateIdentifierOrderByVersionDesc(String aggregateIdentifier);
}
