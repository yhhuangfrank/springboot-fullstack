package com.frank.springbootwithdocker.journey;

import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.request.CustomerRegisterRequest;
import com.frank.springbootwithdocker.request.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "/api/v1/customers";

    @Test
    void canRegisterCustomer() {
        // create register request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String fullName = fakerName.fullName();
        String email = fullName + "-" + UUID.randomUUID() + "@integrationTest.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegisterRequest request = new CustomerRegisterRequest(fullName, email, age);
        // send post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();
        // get all customers
        List<CustomerDto> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDto>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure customer is present
        CustomerDto expected = CustomerDto.builder().name(fullName).email(email).age(age).build();

        assertThat(allCustomers)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id") // 忽略 id 屬性
                .contains(expected); // 包含新建的 Customer
        // get customer by id
        long customerId = allCustomers
                .stream()
                .filter(c -> email.equals(c.getEmail()))
                .findFirst()
                .map(CustomerDto::getId)
                .orElseThrow();

        expected.setId(customerId);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDto>() {
                })
                .isEqualTo(expected);
    }

    @Test
    void canDeleteCustomer() {
        // create register request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String fullName = fakerName.fullName();
        String email = fullName + "-" + UUID.randomUUID() + "@integrationTest.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegisterRequest request = new CustomerRegisterRequest(fullName, email, age);
        // send post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();
        // get all customers
        List<CustomerDto> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDto>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(allCustomers).isNotEmpty();

        // delete customer by id
        long customerId = allCustomers
                .stream()
                .filter(c -> email.equals(c.getEmail()))
                .findFirst()
                .map(CustomerDto::getId)
                .orElseThrow();
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create register request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String fullName = fakerName.fullName();
        String email = fullName + "-" + UUID.randomUUID() + "@integrationTest.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegisterRequest request = new CustomerRegisterRequest(fullName, email, age);
        // send post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();
        // get all customers
        List<CustomerDto> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDto>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(allCustomers).isNotEmpty();

        // update customer by id
        long customerId = allCustomers
                .stream()
                .filter(c -> email.equals(c.getEmail()))
                .findFirst()
                .map(CustomerDto::getId)
                .orElseThrow();
        String newName = "newName";
        String newEmail = "new@email.com";
        int newAge = 77;
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, newEmail, newAge);
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by id
        CustomerDto expected = CustomerDto.builder().id(customerId).name(newName).email(newEmail).age(newAge).build();
        CustomerDto updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(updatedCustomer).isEqualTo(expected);
    }
}
