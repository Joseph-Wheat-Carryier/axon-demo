package com.axion.demo.aggregate.entity;

import com.axion.demo.aggregate.ContractInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContractView implements ContractInterface {
    @Id
    @Column(length = 64)
    private Long identifier;

    private String name;

    private String partyA;

    private String partyB;

    private boolean deleted = false;

}
