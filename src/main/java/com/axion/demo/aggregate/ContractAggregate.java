package com.axion.demo.aggregate;


import com.axion.demo.command.CreateContractCommand;
import com.axion.demo.command.DeleteContractCommand;
import com.axion.demo.command.UpdateContractCommand;
import com.axion.demo.event.ContractCreatedEvent;
import com.axion.demo.event.ContractDeletedEvent;
import com.axion.demo.event.ContractUpdatedEvent;
import com.axion.demo.util.SonyFlakeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.DependsOn;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Aggregate(repository = "contractAggregateRepository")
public class ContractAggregate implements ContractInterface {
    @AggregateIdentifier
    private Long identifier;

    private String name;

    private String partyA;

    private String partyB;

    private boolean deleted = false;

    @CommandHandler
    public ContractAggregate(CreateContractCommand command, MetaData metaData) {
        if (null == command.getIdentifier()) {
            command.setIdentifier(SonyFlakeUtil.getNextLong());
        }
        AggregateLifecycle.apply(new ContractCreatedEvent(command.getIdentifier(), command.getName(), command.getPartyA(), command.getPartyB()), metaData);
    }

    @CommandHandler
    private void on(UpdateContractCommand command, MetaData metaData) {
        AggregateLifecycle.apply(new ContractUpdatedEvent(command.getIdentifier(), command.getName(), command.getPartyA(), command.getPartyB()), metaData);
    }

    @CommandHandler
    private void on(DeleteContractCommand command, MetaData metaData) {
        AggregateLifecycle.apply(new ContractDeletedEvent(command.getIdentifier()), metaData);
    }

    @EventSourcingHandler
    private void on(ContractCreatedEvent event) {
        this.setIdentifier(event.getIdentifier());
        this.onUpdate(event);
    }

    @EventSourcingHandler
    private void onUpdate(ContractUpdatedEvent event) {
        this.setName(event.getName());
        this.setPartyA(event.getPartyA());
        if (event.getPartyB() != null) {
            this.setPartyB(event.getPartyB());
        }
    }

    @EventSourcingHandler(payloadType = ContractDeletedEvent.class)
    private void on() {
        this.setDeleted(true);
    }
}
