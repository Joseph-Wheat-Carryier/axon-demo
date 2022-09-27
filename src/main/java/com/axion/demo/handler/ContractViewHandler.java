//package com.axion.demo.handler;
//
//import com.axion.demo.aggregate.ContractAggregate;
//import com.axion.demo.aggregate.entity.ContractView;
//import com.axion.demo.event.AbstractEvent;
//import com.axion.demo.repository.ContractViewRepository;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.axonframework.eventhandling.DomainEventMessage;
//import org.axonframework.eventhandling.EventHandler;
//import org.axonframework.eventsourcing.EventSourcedAggregate;
//import org.axonframework.eventsourcing.EventSourcingRepository;
//import org.axonframework.modelling.command.LockAwareAggregate;
//import org.springframework.stereotype.Component;
//
//import javax.transaction.Transactional;
//import java.text.MessageFormat;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//@Transactional
//public class ContractViewHandler {
//    private final EventSourcingRepository<ContractAggregate> customEventSourcingRepository;
//
//    private final ContractViewRepository contractAggregateRepository;
//
//    @EventHandler
//    public void on(AbstractEvent event, DomainEventMessage<AbstractEvent> message) {
//        log.info("{}:{}, seq: {}, payload: {}", message.getType(), message.getAggregateIdentifier(), message.getSequenceNumber(), message.getPayload());
//
//        updateContractView(message.getAggregateIdentifier());
//    }
//
//    public void updateContractView(String id) {
//        LockAwareAggregate<ContractAggregate, EventSourcedAggregate<ContractAggregate>> lockAwareAggregate = customEventSourcingRepository.load(id);
//        ContractAggregate aggregate = lockAwareAggregate.getWrappedAggregate().getAggregateRoot();
//
//        ContractView contractView =  contractAggregateRepository.findById(Long.valueOf(id)).orElse(new ContractView());
//        contractView.setIdentifier(aggregate.getIdentifier());
//        contractView.setDeleted(aggregate.isDeleted());
//        contractView.setName(aggregate.getName());
//        contractView.setPartyA(aggregate.getPartyA());
//        contractView.setPartyB(aggregate.getPartyB());
//
//        contractAggregateRepository.save(contractView);
//    }
//}
