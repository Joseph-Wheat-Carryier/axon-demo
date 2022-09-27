package com.axion.demo.repository;

import com.axion.demo.aggregate.entity.ContractView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractViewRepository extends JpaRepository<ContractView, Long> {

}
