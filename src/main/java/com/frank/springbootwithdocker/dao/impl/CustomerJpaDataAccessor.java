package com.frank.springbootwithdocker.dao.impl;

import com.frank.springbootwithdocker.converter.CustomerConverter;
import com.frank.springbootwithdocker.dao.CustomerDao;
import com.frank.springbootwithdocker.entity.Customer;
import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
@RequiredArgsConstructor
public class CustomerJpaDataAccessor implements CustomerDao {
    private final CustomerRepository customerRepository;
    private final CustomerConverter customerConverter;

    @Override
    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream().map(customerConverter::toDTO).toList();
    }

    @Override
    public Optional<CustomerDto> findById(Integer customerId) {
        return customerRepository
                .findById(customerId)
                .map(customerConverter::toDTO);
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
    public void updateCustomer(CustomerDto customerDto) {
        customerRepository.save(customerConverter.toEntity(customerDto));
    }

}
