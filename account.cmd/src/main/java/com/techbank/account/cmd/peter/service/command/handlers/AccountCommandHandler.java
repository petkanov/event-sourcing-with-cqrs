package com.techbank.account.cmd.peter.service.command.handlers;

import com.techbank.account.cmd.peter.model.command.*;
import com.techbank.account.cmd.peter.service.AccountAggregate;
import com.techbank.account.cmd.peter.service.command.Command;
import com.techbank.account.common.handlers.CommandHandler;
import com.techbank.account.common.domain.AggregateRoot;
import com.techbank.account.common.events.BaseEvent;
import com.techbank.account.common.infrastructure.EventStore;
import com.techbank.account.common.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
public class AccountCommandHandler implements CommandHandler {
    @Autowired
    private EventStore eventStore;
    @Autowired
    private EventProducer eventProducer;

    @Command(OpenAccountCommand.class)
    public void handle(OpenAccountCommand command) {
        AccountAggregate aggregate = new AccountAggregate(command);

        save(aggregate);
    }

    @Command(DepositFundsCommand.class)
    public void handle(DepositFundsCommand command) {
        AccountAggregate aggregate = getById(command.getId());

        aggregate.depositFunds(command.getAmount());

        save(aggregate);
    }

    @Command(WithdrawFundsCommand.class)
    public void handle(WithdrawFundsCommand command) {
        AccountAggregate aggregate = getById(command.getId());

        if (command.getAmount() > aggregate.getBalance()) {
            throw new IllegalStateException("Withdrawal declined, insufficient funds!");
        }
        aggregate.withdrawFunds(command.getAmount());

        save(aggregate);
    }

    @Command(CloseAccountCommand.class)
    public void handle(CloseAccountCommand command) {
        AccountAggregate aggregate = getById(command.getId());

        aggregate.closeAccount();

        save(aggregate);
    }

    @Command(RestoreReadDbCommand.class)
    public void handle(RestoreReadDbCommand command) {
        republishEvents();
    }


    private void save(AggregateRoot aggregate) {
        eventStore.saveEvents(aggregate);

        aggregate.markChangesAsCommitted();
    }

    private AccountAggregate getById(String aggregateId) {
        AccountAggregate aggregate = new AccountAggregate();

        List<BaseEvent> events = eventStore.getAggregateEventsInAscOrder(aggregateId);

        if (!CollectionUtils.isEmpty(events)) {
            aggregate.replayEvents(events);
        }
        return aggregate;
    }

    private void republishEvents() {
        List<String> aggregateIds = eventStore.getAggregateIds();

        for(String aggregateId: aggregateIds) {
            AccountAggregate aggregate = getById(aggregateId);
            if (!aggregate.getActive()) {
                continue;
            }
            List<BaseEvent> events = eventStore.getAggregateEventsInAscOrder(aggregateId);

            for(BaseEvent event: events) {
                eventProducer.produce(event.getClass().getSimpleName(), event);
            }
        }
    }
}
