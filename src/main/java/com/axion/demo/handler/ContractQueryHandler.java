package com.axion.demo.handler;

import com.axion.demo.aggregate.ContractAggregate;
import com.axion.demo.command.QueryContractCommand;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.modelling.command.Aggregate;
import org.axonframework.modelling.command.Repository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ContractQueryHandler {
    private final EventSourcingRepository<ContractAggregate> contractAggregateRepository;

    @QueryHandler
    public ContractAggregate on(QueryContractCommand command) {
        ContractAggregate contractAggregate = contractAggregateRepository.load(command.getId()).getWrappedAggregate().getAggregateRoot();
        System.out.println(contractAggregate);
        return contractAggregate;
    }
}
