package com.frank.springbootwithdocker;

import com.frank.springbootwithdocker.entity.Customer;
import com.frank.springbootwithdocker.repository.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        customerRepository.deleteAllInBatch();
        Faker faker = new Faker();
        Random random = new Random();
        String lastName = faker.name().lastName();
        String firstName = faker.name().firstName();
        String name = firstName + " " + lastName;
        String email = firstName + "." + lastName + "@example.com";
        int age = random.nextInt(16, 99);
        Customer customer = Customer.builder().name(name).email(email).age(age).build();
        customerRepository.save(customer);
    }
}
