package com.frank.springbootwithdocker;

import com.frank.springbootwithdocker.converter.CustomerConverter;
import com.frank.springbootwithdocker.entity.Customer;
import com.frank.springbootwithdocker.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SpringbootWithDockerApplication implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerConverter customerConverter;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootWithDockerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Customer frank = Customer.builder().name("Frank").email("frank@gmail.com").age(20).build();
        Customer wendy = Customer.builder().name("Wendy").email("wendy@gmail.com").age(18).build();
//        customerRepository.saveAll(List.of(frank, wendy));
    }
}
