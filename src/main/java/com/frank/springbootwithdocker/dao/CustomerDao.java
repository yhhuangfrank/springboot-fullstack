package com.frank.springbootwithdocker.dao;

import com.frank.springbootwithdocker.dto.CustomerDto;

import java.util.List;

public interface CustomerDao {

    List<CustomerDto> findAll();

    boolean existCustomerWithEmail(String email);

    void insertCustomer(CustomerDto customerDto);

    void deleteById(Integer customerId);

    boolean existCustomerWithId(Integer customerId);

    CustomerDto updateCustomer(CustomerDto customerDto);

    CustomerDto findById(Integer customerId);
}
