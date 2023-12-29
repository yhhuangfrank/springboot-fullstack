package com.frank.springbootwithdocker.dao.impl;

import com.frank.springbootwithdocker.converter.CustomerConverter;
import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.entity.Customer;
import com.frank.springbootwithdocker.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.verify;

class CustomerJpaDataAccessorTest {
    private CustomerJpaDataAccessor underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;
    private final CustomerConverter customerConverter = new CustomerConverter(new ModelMapper());

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this); // 使每個 test 都有新的 mock
        underTest = new CustomerJpaDataAccessor(customerRepository, customerConverter);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void findAll() {
        // When
        underTest.findAll();
        // Then
        verify(customerRepository).findAll(); // 驗證 mock 對象有調用 findAll 方法
    }

    @Test
    void findById() {
        // Given
        long id = 1;
        // When
        underTest.findById(id);
        // Then
        verify(customerRepository).findById(id);
    }

    @Test
    void existCustomerWithEmail() {
        // Given
        String email = "frank@example.com";
        // When
        underTest.existCustomerWithEmail(email);
        // Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void insertCustomer() {
        // Given
        CustomerDto dto = CustomerDto.builder().id(1L).name("frank").build();
        Customer entity = customerConverter.toEntity(dto);
        // When
        underTest.insertCustomer(dto);
        // Then
        verify(customerRepository).save(entity);
    }

    @Test
    void deleteById() {
        // Given
        long id = 1;
        // When
        underTest.deleteById(id);
        // Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void existCustomerWithId() {
        // Given
        long id = 1;
        // When
        underTest.existCustomerWithId(id);
        // Then
        verify(customerRepository).existsById(id);
    }

    @Test
    void updateCustomer() {
        // Given
        CustomerDto dto = CustomerDto.builder().id(1L).name("frank").build();
        Customer entity = customerConverter.toEntity(dto);
        // When
        underTest.updateCustomer(dto);
        // Then
        verify(customerRepository).save(entity);
    }
}