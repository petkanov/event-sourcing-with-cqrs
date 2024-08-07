package com.techbank.account.common.infrastructure;

import com.techbank.account.common.domain.AggregateRoot;
import com.techbank.account.common.events.BaseEvent;

import java.util.List;

public interface EventStore {
    void saveEvents(AggregateRoot aggregate);
    List<BaseEvent> getAggregateEventsInAscOrder(String aggregateId);
    List<String> getAggregateIds();
}
