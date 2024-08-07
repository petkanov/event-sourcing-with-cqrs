package com.techbank.account.common.producers;

import com.techbank.account.common.events.BaseEvent;

public interface EventProducer {
    void produce(String topic, BaseEvent event);
}
