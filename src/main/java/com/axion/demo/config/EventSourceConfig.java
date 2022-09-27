package com.axion.demo.config;

import com.axion.demo.aggregate.ContractAggregate;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.axonframework.eventsourcing.*;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.modelling.command.Repository;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.config.SpringAxonConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
public class EventSourceConfig {
    @Bean(name = "embeddedEventStore")
    public EmbeddedEventStore eventStore(EventStorageEngine storageEngine, SpringAxonConfiguration configuration) {
        return EmbeddedEventStore.builder().
                storageEngine(storageEngine).
                messageMonitor(configuration.getObject().messageMonitor(EventStore.class, "eventStore"))
                .build();
    }

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings
                .builder()
                .applyConnectionString(new ConnectionString("mongodb://admin:admin123456@192.168.10.88:27018/?authSource=admin"))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(5, TimeUnit.SECONDS))
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public EventStorageEngine storageEngine(MongoClient client, Serializer serializer) {
        return MongoEventStorageEngine
                .builder()
                .eventSerializer(serializer)
                .snapshotSerializer(serializer)
                .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
                .build();
    }

    @Bean
    public SnapshotTriggerDefinition snapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }

    @Bean
    public AggregateSnapshotter snapshotter(@Qualifier("embeddedEventStore") EmbeddedEventStore eventStore, ParameterResolverFactory parameterResolverFactory) {
        return AggregateSnapshotter.builder()
                .eventStore(eventStore)
                .parameterResolverFactory(parameterResolverFactory)
                .aggregateFactories(Collections.singletonList(new GenericAggregateFactory<>(ContractAggregate.class)))
                .build();
    }
}
