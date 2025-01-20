package com.example.banking;

import com.example.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface H2Repository extends JpaRepository<Account, Long> {

}
