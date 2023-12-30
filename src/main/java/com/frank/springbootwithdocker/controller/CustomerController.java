package com.frank.springbootwithdocker.controller;

import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.request.CustomerRegisterRequest;
import com.frank.springbootwithdocker.request.CustomerUpdateRequest;
import com.frank.springbootwithdocker.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerDto>> findAll() {
        return ResponseEntity.ok(customerService.findAllCustomers());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> findById(@PathVariable(name = "customerId") Long customerId) {
        return ResponseEntity.ok(customerService.findCustomerById(customerId));
    }

    @PostMapping
    public ResponseEntity<String> checkCustomerWithEmail(
            @RequestBody CustomerRegisterRequest customerRegisterRequest
    ) {
        customerService.addCustomer(customerRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomerById(
            @PathVariable(name = "customerId") Long customerId,
            @RequestBody CustomerUpdateRequest customerUpdateRequest
    ) {
        CustomerDto updatedCustomer = customerService.updateCustomer(customerId, customerUpdateRequest);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomerById(
            @PathVariable(name = "customerId") Long customerId
    ) {
        customerService.deleteCustomerById(customerId);
        return ResponseEntity.ok("deleted");
    }


}
