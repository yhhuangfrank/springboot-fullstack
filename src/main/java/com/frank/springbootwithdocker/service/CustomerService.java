package com.frank.springbootwithdocker.service;

import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.exception.DuplicateResourceException;
import com.frank.springbootwithdocker.dao.CustomerDao;
import com.frank.springbootwithdocker.exception.RequestValidationException;
import com.frank.springbootwithdocker.exception.ResourceNotFoundException;
import com.frank.springbootwithdocker.request.CustomerRegisterRequest;
import com.frank.springbootwithdocker.request.CustomerUpdateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void addCustomer (CustomerRegisterRequest customerRegisterRequest) {
        // check if email exists
        if (customerDao.existCustomerWithEmail(customerRegisterRequest.email())) {
            throw new DuplicateResourceException("Customer with current email already exist!");
        }
        customerDao.insertCustomer(this.mapRequestToDTO(customerRegisterRequest));
    }

    public void deleteCustomerById(Integer customerId) {
        if (!customerDao.existCustomerWithId(customerId)) {
            throw new ResourceNotFoundException("Customer with id: [%s] is not exist!".formatted(customerId));
        }
        customerDao.deleteById(customerId);
    }

    public List<CustomerDto> findAll() {
        return customerDao.findAll();
    }

    public CustomerDto findById(Integer customerId) {
        return customerDao
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id: [%s] is not exist!".formatted(customerId)));
    }

    public CustomerDto updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest) {
        boolean isChange = false;
        CustomerDto customerDto = this.findById(customerId);
        if (updateRequest.name() != null && !updateRequest.name().equals(customerDto.getName())) {
            customerDto.setName(updateRequest.name());
            isChange = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(customerDto.getEmail())) {
            customerDto.setEmail(updateRequest.email());
            isChange = true;
        }

        if (updateRequest.age() != null && !updateRequest.age().equals(customerDto.getAge())) {
            customerDto.setAge(updateRequest.age());
            isChange = true;
        }

        if (!isChange) {
            throw new RequestValidationException("no data changes found!");
        }

        customerDao.updateCustomer(customerDto);
        return customerDto;
    }

    private CustomerDto mapRequestToDTO(CustomerRegisterRequest customerRegisterRequest) {
        return CustomerDto.builder()
                .name(customerRegisterRequest.name())
                .email(customerRegisterRequest.email())
                .age(customerRegisterRequest.age())
                .build();
    }
}
