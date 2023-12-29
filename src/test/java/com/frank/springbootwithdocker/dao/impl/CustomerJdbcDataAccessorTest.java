package com.frank.springbootwithdocker.dao.impl;

import com.frank.springbootwithdocker.BaseTestContainers;
import com.frank.springbootwithdocker.converter.CustomerConverter;
import com.frank.springbootwithdocker.rowmapper.CustomerRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

class CustomerJdbcDataAccessorTest extends BaseTestContainers {
    private CustomerJdbcDataAccessor underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    private final CustomerConverter customerConverter = new CustomerConverter(new ModelMapper());

    @BeforeEach
    void setUp() {
        underTest = new CustomerJdbcDataAccessor(
                BaseTestContainers.getJdbcTemplate(),
                customerRowMapper,
                customerConverter
        );
    }

    @Test
    void findAll() {
    }

    @Test
    void existCustomerWithEmail() {
    }

    @Test
    void insertCustomer() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void existCustomerWithId() {
    }

    @Test
    void updateCustomer() {
    }

    @Test
    void findById() {
    }
}