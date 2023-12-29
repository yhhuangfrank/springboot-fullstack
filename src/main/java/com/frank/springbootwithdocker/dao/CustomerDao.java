package com.frank.springbootwithdocker.dao;

import com.frank.springbootwithdocker.dto.CustomerDto;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<CustomerDto> findAll();

    boolean existCustomerWithEmail(String email);

    void insertCustomer(CustomerDto customerDto);

    void deleteById(Integer customerId);

    boolean existCustomerWithId(Integer customerId);

    void updateCustomer(CustomerDto customerDto);

    Optional<CustomerDto> findById(Integer customerId);
}
