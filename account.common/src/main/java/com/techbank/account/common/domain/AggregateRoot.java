package com.techbank.account.common.domain;

import com.techbank.account.common.events.BaseEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class AggregateRoot {
    public static final String APPLY_METHOD_NAME = "apply";

    @Getter
    protected String id;

    private final AtomicInteger version = new AtomicInteger(-1);

    private final List<BaseEvent> changes = new ArrayList<>();

    public List<BaseEvent> getUncommittedChanges() {
        return this.changes;
    }

    public int getAggregateVersion() {
        return version.get();
    }

    public void setAggregateVersion(int value) {
        if (value > 0) {
            version.set(value);
        }
    }

    public void incrementAggregateVersion() {
        version.incrementAndGet();
    }

    public void markChangesAsCommitted() {
        this.changes.clear();
    }

    private void applyEvent(BaseEvent event) {
        Method method = ReflectionUtils.findMethod(getClass(), APPLY_METHOD_NAME, event.getClass());
        if (method == null) {
            log.error("[{}] method implementation is missing for [{}]", APPLY_METHOD_NAME, event.getClass().getName());
            return;
        }
        ReflectionUtils.invokeMethod(method, this, event);
    }

    public void raiseEvent(BaseEvent event) {
        applyEvent(event);

        changes.add(event);
    }

    public void replayEvents(Iterable<BaseEvent> events) {
        for (BaseEvent event : events) {
            applyEvent(event);

            setAggregateVersion(event.getVersion());
        }
    }
}
