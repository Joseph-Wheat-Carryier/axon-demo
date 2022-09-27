package com.axion.demo.config;

import com.axion.demo.aggregate.ContractAggregate;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Bean
    public EventSourcingRepository<ContractAggregate> contractAggregateRepository(@Qualifier("embeddedEventStore") EventStore eventStore,
                                                                                  SnapshotTriggerDefinition snapshotTriggerDefinition) {
        return EventSourcingRepository
                .builder(ContractAggregate.class)
                .eventStore(eventStore)
                .snapshotTriggerDefinition(snapshotTriggerDefinition)
                .build();
    }
}
