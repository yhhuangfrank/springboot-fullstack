package com.frank.springbootwithdocker.repository;

import com.frank.springbootwithdocker.BaseTestContainers;
import com.frank.springbootwithdocker.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 讓 springBoot 不做預設的 test db 配置
class CustomerRepositoryTest extends BaseTestContainers { // 繼承來自行配置測試用 db

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    public void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsCustomerByEmail_AndReturnTrue() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        int age = new Random().nextInt(10, 99);
        Customer customer = Customer.builder().name(name).email(email).age(age).build();
        underTest.save(customer);
        // When
        boolean actual = underTest.existsCustomerByEmail(email);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmail_AndReturnFalse() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        // When
        boolean actual = underTest.existsCustomerByEmail(email);
        // Then
        assertThat(actual).isFalse();
    }
}