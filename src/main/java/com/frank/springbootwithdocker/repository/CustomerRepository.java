package com.frank.springbootwithdocker.repository;

import com.frank.springbootwithdocker.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsCustomerByEmail(String email);
}