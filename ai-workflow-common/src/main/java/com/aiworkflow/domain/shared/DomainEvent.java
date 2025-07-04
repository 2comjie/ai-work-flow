package com.aiworkflow.domain.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * 领域事件基类
 * 所有业务事件都应该继承此类
 */
public abstract class DomainEvent {
    
    @JsonProperty("eventId")
    private final String eventId;
    
    @JsonProperty("eventType")
    private final String eventType;
    
    @JsonProperty("occurredOn")
    private final Instant occurredOn;
    
    @JsonProperty("aggregateId")
    private final String aggregateId;
    
    @JsonProperty("version")
    private final Integer version;
    
    protected DomainEvent(String eventType, String aggregateId, Integer version) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.occurredOn = Instant.now();
        this.aggregateId = aggregateId;
        this.version = version;
    }
    
    // Getters
    public String getEventId() {
        return eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    public String getAggregateId() {
        return aggregateId;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    @JsonIgnore
    public abstract Object getPayload();
    
    @Override
    public String toString() {
        return String.format("DomainEvent{eventId='%s', eventType='%s', aggregateId='%s', occurredOn=%s}", 
            eventId, eventType, aggregateId, occurredOn);
    }
}