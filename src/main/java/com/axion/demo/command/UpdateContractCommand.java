package com.axion.demo.command;


import com.axion.demo.aggregate.AbstractCommand;
import com.axion.demo.aggregate.ContractInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateContractCommand extends AbstractCommand implements ContractInterface {

    private String name;

    private String partyA;

    private String partyB;

    public UpdateContractCommand(Long identifier, String name, String partyA, String partyB) {
        super(identifier);
        this.name = name;
        this.partyA = partyA;
        this.partyB = partyB;
    }
}