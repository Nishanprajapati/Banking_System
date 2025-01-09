package com.example.banking.entity;


import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="address")
public class Address {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    private String city;
    private String addressType;
}
