package com.frank.springbootwithdocker.dao.impl;

import com.frank.springbootwithdocker.converter.CustomerConverter;
import com.frank.springbootwithdocker.dao.CustomerDao;
import com.frank.springbootwithdocker.entity.Customer;
import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.exception.ResourceNotFoundException;
import com.frank.springbootwithdocker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomerJpaDataAccessor implements CustomerDao {
    private final CustomerRepository customerRepository;
    private final CustomerConverter customerConverter;

    @Override
    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream().map(customerConverter::toDTO).toList();
    }

    @Override
    public CustomerDto findById(Integer customerId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id: [%s] is not exist!".formatted(customerId)));
        return customerConverter.toDTO(customer);
    }

    @Override
    public boolean existCustomerWithEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    @Override
    public void insertCustomer(CustomerDto customerDto) {
        Customer newCustomer = customerConverter.toEntity(customerDto);
        customerRepository.save(newCustomer);
    }

    @Override
    public void deleteById(Integer customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public boolean existCustomerWithId(Integer customerId) {
        return customerRepository.existsById(customerId);
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        Customer updatedCustomer = customerRepository.save(customerConverter.toEntity(customerDto));
        return customerConverter.toDTO(updatedCustomer);
    }


}
