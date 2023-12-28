package com.frank.springbootwithdocker.service;

import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.exception.DuplicateResourceException;
import com.frank.springbootwithdocker.dao.CustomerDao;
import com.frank.springbootwithdocker.exception.RequestValidationException;
import com.frank.springbootwithdocker.exception.ResourceNotFoundException;
import com.frank.springbootwithdocker.request.CustomerRegisterRequest;
import com.frank.springbootwithdocker.request.CustomerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerDao customerDao;

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
        return customerDao.findById(customerId);
    }

    public CustomerDto updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest) {
        boolean isChange = false;
        CustomerDto foundCustomer = this.findById(customerId);
        if (updateRequest.name() != null && !updateRequest.name().equals(foundCustomer.getName())) {
            foundCustomer.setName(updateRequest.name());
            isChange = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(foundCustomer.getEmail())) {
            foundCustomer.setEmail(updateRequest.email());
            isChange = true;
        }

        if (updateRequest.age() != null && !updateRequest.age().equals(foundCustomer.getAge())) {
            foundCustomer.setAge(updateRequest.age());
            isChange = true;
        }

        if (!isChange) {
            throw new RequestValidationException("no data changes found!");
        }
        return customerDao.updateCustomer(foundCustomer);
    }

    private CustomerDto mapRequestToDTO(CustomerRegisterRequest customerRegisterRequest) {
        return CustomerDto.builder()
                .name(customerRegisterRequest.name())
                .email(customerRegisterRequest.email())
                .age(customerRegisterRequest.age())
                .build();
    }
}
