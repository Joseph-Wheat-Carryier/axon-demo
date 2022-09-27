package com.axion.demo.controller;

import com.axion.demo.aggregate.ContractAggregate;
import com.axion.demo.command.CreateContractCommand;
import com.axion.demo.command.DeleteContractCommand;
import com.axion.demo.command.QueryContractCommand;
import com.axion.demo.command.UpdateContractCommand;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/contracts")
@AllArgsConstructor
public class ContractController {
    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    @PutMapping
    public void createContract(@RequestBody @Valid CreateContractCommand command) {
        commandGateway.send(command);
    }

    @PostMapping("/{id}")
    public void updateContract(@PathVariable("id") Long id, @RequestBody @Valid UpdateContractCommand command) {
        command.setIdentifier(id);
        commandGateway.send(command);
    }

    @DeleteMapping("/{id}")
    public void deleteContract(@PathVariable("id") Long id) {
        commandGateway.send(new DeleteContractCommand(id));
    }

    @GetMapping("/{id}")
    public ContractAggregate getContract(@PathVariable("id") Long id) {
        QueryContractCommand command = new QueryContractCommand(id.toString(), Instant.now());

        return queryGateway.query(command, ContractAggregate.class).join();
    }
}
