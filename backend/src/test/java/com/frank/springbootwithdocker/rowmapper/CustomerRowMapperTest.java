package com.frank.springbootwithdocker.rowmapper;

import com.frank.springbootwithdocker.entity.Customer;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        // Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class); // 模擬 ResultSet
        Long id = 1L;
        String name = "frank";
        String email = "frank@email.com";
        Integer age = 20;
        when(resultSet.getLong("id")).thenReturn(id);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getString("email")).thenReturn(email);
        when(resultSet.getInt("age")).thenReturn(age);
        // When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);
        // Then
        Customer expected = Customer.builder()
                .id(id)
                .name(name)
                .email(email)
                .age(age)
                .build();
        assertThat(actual).isNotNull().isEqualTo(expected);
    }
}