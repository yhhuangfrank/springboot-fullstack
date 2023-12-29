package com.frank.springbootwithdocker.dao.impl;

import com.frank.springbootwithdocker.BaseTestContainers;
import com.frank.springbootwithdocker.converter.CustomerConverter;
import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.rowmapper.CustomerRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerJdbcDataAccessorTest extends BaseTestContainers {
    private CustomerJdbcDataAccessor underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    private final CustomerConverter customerConverter = new CustomerConverter(new ModelMapper());
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJdbcDataAccessor(
                BaseTestContainers.getJdbcTemplate(),
                customerRowMapper,
                customerConverter
        );
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        int age = new Random().nextInt(10, 99);
        customerDto = CustomerDto.builder().name(name).email(email).age(age).build();
    }

    @Test
    void findAll() {
        // Given
        underTest.insertCustomer(customerDto);

        // When
        List<CustomerDto> actual = underTest.findAll();

        // Then
        assertThat(actual).isNotEmpty();
    }


    @Test
    void existCustomerWithEmail_AndReturnTrue() {
        // Given
        String email = customerDto.getEmail();
        underTest.insertCustomer(customerDto);
        // When
        boolean actual = underTest.existCustomerWithEmail(email);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existCustomerWithEmail_AndReturnFalse() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        underTest.insertCustomer(customerDto);
        // When
        boolean actual = underTest.existCustomerWithEmail(email);
        // Then
        assertThatNoException().isThrownBy(() -> underTest.existCustomerWithEmail(email));
        assertThat(actual).isFalse();
    }

    @Test
    void insertCustomer() {
        // Given
        String email = customerDto.getEmail();
        // When
        underTest.insertCustomer(customerDto);
        // Then
        int customerId = underTest.findAll()
                .stream()
                .filter(c -> email.equals(c.getEmail()))
                .findFirst()
                .map(CustomerDto::getId)
                .orElseThrow();
        Optional<CustomerDto> actual = underTest.findById(customerId);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customerDto.getName());
            assertThat(c.getEmail()).isEqualTo(customerDto.getEmail());
            assertThat(c.getAge()).isEqualTo(customerDto.getAge());
        });
    }
    @Test
    void insertCustomerWithDuplicateEmail() {
        // Given
        underTest.insertCustomer(customerDto);
        // When

        // Then
        assertThrows(Exception.class, () -> underTest.insertCustomer(customerDto));
    }

    @Test
    void deleteById() {
        // Given
        underTest.insertCustomer(customerDto);
        int customerId = underTest.findAll()
                .stream()
                .filter(c -> customerDto.getEmail().equals(c.getEmail()))
                .findFirst()
                .map(CustomerDto::getId)
                .orElseThrow();
        // When
        underTest.deleteById(customerId);
        // Then
        Optional<CustomerDto> actual = underTest.findById(customerId);
        assertThat(actual).isEmpty();
    }

    @Test
    void existCustomerWithId() {
        // Given
        underTest.insertCustomer(customerDto);
        String email = customerDto.getEmail();
        int customerId = underTest.findAll()
                .stream()
                .filter(c -> email.equals(c.getEmail()))
                .findFirst()
                .map(CustomerDto::getId)
                .orElseThrow();
        // When
        boolean actual = underTest.existCustomerWithId(customerId);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void updateCustomer() {
        // Given
        String newName = "newName";
        String newEmail = "newEmail@example.com";
        int newAge = 100;
        underTest.insertCustomer(customerDto);
        int customerId = underTest.findAll()
                .stream()
                .filter(c -> customerDto.getEmail().equals(c.getEmail()))
                .findFirst()
                .map(CustomerDto::getId)
                .orElseThrow();
        CustomerDto dbCustomer = underTest.findById(customerId).orElseThrow();
        CustomerDto newCustomer = CustomerDto.builder().id(dbCustomer.getId()).name(newName).email(newEmail).age(newAge).build();
        // When
        underTest.updateCustomer(newCustomer);
        // Then
        Optional<CustomerDto> actual = underTest.findById(customerId);
        assertThat(actual).hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void findById() {
        // Given
        underTest.insertCustomer(customerDto);
        int customerId = underTest.findAll()
                .stream()
                .filter(c -> customerDto.getEmail().equals(c.getEmail()))
                .findFirst()
                .map(CustomerDto::getId)
                .orElseThrow();
        // When
        Optional<CustomerDto> actual = underTest.findById(customerId);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customerDto.getName());
            assertThat(c.getEmail()).isEqualTo(customerDto.getEmail());
            assertThat(c.getAge()).isEqualTo(customerDto.getAge());
        });
    }

    @Test
    void findByWrongId() {
        // Given
        int customerId = -1;
        // When
        Optional<CustomerDto> actual = underTest.findById(customerId);
        // Then
        assertThat(actual).isEmpty();
    }
}