package com.techbank.account.cmd.peter.service.event;

import com.techbank.account.cmd.peter.service.AccountAggregate;
import com.techbank.account.cmd.peter.repository.EventStoreRepository;
import com.techbank.account.common.domain.AggregateRoot;
import com.techbank.account.common.events.BaseEvent;
import com.techbank.account.common.events.EventModel;
import com.techbank.account.common.exceptions.AggregateNotFoundException;
import com.techbank.account.common.exceptions.ConcurrencyException;
import com.techbank.account.common.infrastructure.EventStore;
import com.techbank.account.common.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountEventStore implements EventStore {
    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private EventStoreRepository eventStoreRepository;

    @Override
    public void saveEvents(AggregateRoot aggregate) {
        String aggregateId = aggregate.getId();
        Iterable<BaseEvent> events = aggregate.getUncommittedChanges();

        Optional<EventModel> lastAggregateEvent = eventStoreRepository
                .findFirstByAggregateIdentifierOrderByVersionDesc(aggregateId);

        if (lastAggregateEvent.isPresent() &&
                lastAggregateEvent.get().getVersion() != aggregate.getAggregateVersion()) {
            throw new ConcurrencyException();
        }

        for (BaseEvent event : events) {
            final int newEventVersion = aggregate.getAggregateVersion() + 1;

            event.setVersion(newEventVersion);

            EventModel eventModel = EventModel.builder()
                    .timeStamp(new Date())
                    .aggregateIdentifier(aggregateId)
                    .aggregateType(AccountAggregate.class.getTypeName())
                    .version(newEventVersion)
                    .eventType(event.getClass().getTypeName())
                    .eventData(event)
                    .build();

            eventStoreRepository.save(eventModel);

            eventProducer.produce(event.getClass().getSimpleName(), event);

            aggregate.setAggregateVersion(newEventVersion);
        }
    }

    @Override
    public List<BaseEvent> getAggregateEventsInAscOrder(String aggregateId) {
        List<EventModel> eventStream = eventStoreRepository.findByAggregateIdentifierOrderByVersionAsc(aggregateId);

        if (eventStream == null || eventStream.isEmpty()) {
            throw new AggregateNotFoundException("Incorrect account ID provided!");
        }
        return eventStream.stream().map(EventModel::getEventData).collect(Collectors.toList());
    }

    @Override
    public List<String> getAggregateIds() {
        var eventStream = eventStoreRepository.findAll();
        if (eventStream.isEmpty()) {
            throw new IllegalStateException("Could not retrieve event stream from the event store!");
        }
        return eventStream.stream().map(EventModel::getAggregateIdentifier).distinct().collect(Collectors.toList());
    }
}
