package com.frank.springbootwithdocker.dao.impl;

import com.frank.springbootwithdocker.converter.CustomerConverter;
import com.frank.springbootwithdocker.dao.CustomerDao;
import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.entity.Customer;
import com.frank.springbootwithdocker.rowmapper.CustomerRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
@RequiredArgsConstructor
public class CustomerJdbcDataAccessor implements CustomerDao {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;
    private final CustomerConverter customerConverter;

    @Override
    public List<CustomerDto> findAll() {
        String sql = """
                SELECT id, name, email, age
                FROM customer;
                """;
        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);
        return customers.stream().map(customerConverter::toDTO).toList();
    }

    @Override
    public boolean existCustomerWithEmail(String email) {
        String sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count;
        try {
            count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        } catch (DataAccessException e) {
            count = 0;
        }
        return count != null && count > 0;
    }

    @Override
    public void insertCustomer(CustomerDto customerDto) {
        String sql = """
                INSERT INTO customer (name, email, age)
                VALUES (?,?,?);
                """;
        jdbcTemplate.update(sql, customerDto.getName(), customerDto.getEmail(), customerDto.getAge());
    }

    @Override
    public void deleteById(Integer customerId) {
        String sql = """
                DELETE FROM customer
                WHERE id = ?;
                """;
        jdbcTemplate.update(sql, customerId);
    }

    @Override
    public boolean existCustomerWithId(Integer customerId) {
        String sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE id = ?;
                """;
        Integer count;
        try {
            count = jdbcTemplate.queryForObject(sql, Integer.class, customerId);
        } catch (DataAccessException e) {
            count = 0;
        }
        return count != null && count > 0;
    }

    @Override
    public void updateCustomer(CustomerDto customerDto) {
        String sql = """
                UPDATE customer
                SET name = ?, email = ?, age = ?
                WHERE id = ?;
                """;
        jdbcTemplate.update(sql, customerDto.getName(), customerDto.getEmail(), customerDto.getAge(), customerDto.getId());
    }

    @Override
    public Optional<CustomerDto> findById(Integer customerId) {
        String sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?;
                """;

        return jdbcTemplate.query(sql, customerRowMapper, customerId)
                .stream()
                .findFirst()
                .map(customerConverter::toDTO);
    }
}
