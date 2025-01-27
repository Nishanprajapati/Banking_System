package com.example.banking.dto;

import com.example.banking.entity.Address;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class AccountDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

   @NotBlank
   @Size(min = 3, max = 50,message="Name must be of at least 3 character ")
    private String accountHolderName;


    @NotNull(message = "Balance cannot be null")
    @Min(value = 100, message = "Balance must be greater than or equal to 100")
    private Double balance;

    private LocalDateTime createdAt;

    @NotEmpty(message = "Password cannot be empty")
    private String password;


    private Address address;

}
