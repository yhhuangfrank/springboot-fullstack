package com.frank.springbootwithdocker.service;

import com.frank.springbootwithdocker.dao.CustomerDao;
import com.frank.springbootwithdocker.dto.CustomerDto;
import com.frank.springbootwithdocker.exception.DuplicateResourceException;
import com.frank.springbootwithdocker.exception.RequestValidationException;
import com.frank.springbootwithdocker.exception.ResourceNotFoundException;
import com.frank.springbootwithdocker.request.CustomerRegisterRequest;
import com.frank.springbootwithdocker.request.CustomerUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 省去需要使用 autocloseable.close()
class CustomerServiceTest {
    private CustomerService underTest;
    @Mock
    private CustomerDao customerDao;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
        customerDto = CustomerDto.builder().id(1L).name("Frank").email("frank@example.com").age(27).build();
    }

    @Test
    void addCustomer_AndEmailNotExist() {
        // Given
        String name = "frank";
        int age = 19;
        String notExistEmail = "notExist@email.com";
        CustomerRegisterRequest request = new CustomerRegisterRequest(name, notExistEmail, age);
        when(customerDao.existCustomerWithEmail(notExistEmail)).thenReturn(false);
        // When
        underTest.addCustomer(request);
        // Then
        ArgumentCaptor<CustomerDto> customerDtoArgumentCaptor = ArgumentCaptor.forClass(CustomerDto.class);

        verify(customerDao).insertCustomer(customerDtoArgumentCaptor.capture()); // 捕獲當 insertCustomer 時所傳的參數

        CustomerDto capturedCustomer = customerDtoArgumentCaptor.getValue();
        assertThat(capturedCustomer).isNotNull();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(notExistEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
    }

    @Test
    void addCustomer_AndEmailExist() {
        // Given
        String name = "frank";
        int age = 19;
        String existEmail = "exist@email.com";
        CustomerRegisterRequest request = new CustomerRegisterRequest(name, existEmail, age);
        when(customerDao.existCustomerWithEmail(existEmail)).thenReturn(true);
        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with current email already exist!");
        // Then
        verify(customerDao, never()).insertCustomer(any()); // 驗證 dao 沒有傳入任何值
    }

    @Test
    void deleteCustomerById_AndCustomerIdNotExist() {
        // Given
        long customerId = 1L;
        when(customerDao.existCustomerWithId(customerId)).thenReturn(false);
        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id: [%s] is not exist!".formatted(customerId));
        // Then
        verify(customerDao, never()).deleteById(any());
    }

    @Test
    void deleteCustomerById_AndCustomerIdExist() {
        // Given
        long customerId = 1L;
        when(customerDao.existCustomerWithId(customerId)).thenReturn(true);
        // When
        underTest.deleteCustomerById(customerId);
        // Then
        verify(customerDao).deleteById(customerId);
    }

    @Test
    void findAll() {
        // When
        underTest.findAllCustomers();
        // Then
        verify(customerDao).findAll();
    }

    @Test
    void findById_AndReturn() {
        // Given
        Long customerId = 1L;
        when(customerDao.findById(customerId)).thenReturn(Optional.ofNullable(customerDto)); // 填充回傳值
        // When
        CustomerDto actual = underTest.findCustomerById(customerId);
        // Then
        assertThat(actual).isNotNull().isEqualTo(customerDto);
    }

    @Test
    void findById_AndThrowResourceNotFoundException() {
        // Given
        Long customerId = 1L;
        when(customerDao.findById(customerId)).thenReturn(Optional.empty()); // 填充空回傳值
        // When
        // Then
        assertThatThrownBy(() -> underTest.findCustomerById(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id: [%s] is not exist!".formatted(customerId));
    }

    @Test
    void updateCustomer_AndCustomerIdNotExist() {
        // Given
        long notExistId = 100L;
        when(customerDao.findById(notExistId)).thenReturn(Optional.empty());
        // When
        CustomerUpdateRequest request = any(CustomerUpdateRequest.class);
        assertThatThrownBy(() -> underTest.updateCustomer(notExistId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id: [%s] is not exist!".formatted(notExistId));
        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomer_AndCustomerIdExistButNoFieldChanged() {
        // Given
        long customerId = customerDto.getId();
        when(customerDao.findById(customerId)).thenReturn(Optional.of(customerDto));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customerDto.getName(), customerDto.getEmail(), customerDto.getAge()
        );
        // When
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found!");
        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomer_AndCustomerIdExistButEmailDuplicated() {
        // Given
        long customerId = customerDto.getId();
        String newName = "newName";
        String duplicateEmail = "duplicate@email.com";
        int age = 100;
        when(customerDao.findById(customerId)).thenReturn(Optional.of(customerDto));
        CustomerUpdateRequest request = new CustomerUpdateRequest(newName, duplicateEmail, age);
        when(customerDao.existCustomerWithEmail(duplicateEmail)).thenReturn(true);
        // When
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with current email already exist!");
        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomer_AndCustomerIdExistAndAllFieldUpdated() {
        // Given
        long customerId = customerDto.getId();
        String newName = "newName";
        String newEmail = "newEmail@email.com";
        int age = 100;
        when(customerDao.findById(customerId)).thenReturn(Optional.of(customerDto));
        CustomerUpdateRequest request = new CustomerUpdateRequest(newName, newEmail, age);
        when(customerDao.existCustomerWithEmail(newEmail)).thenReturn(false);
        // When
        underTest.updateCustomer(customerId, request);
        // Then
        ArgumentCaptor<CustomerDto> customerDtoArgumentCaptor = ArgumentCaptor.forClass(CustomerDto.class);

        verify(customerDao).updateCustomer(customerDtoArgumentCaptor.capture());
        CustomerDto captured = customerDtoArgumentCaptor.getValue();

        assertThat(captured).isNotNull();
        assertThat(captured.getId()).isEqualTo(customerId);
        assertThat(captured.getName()).isEqualTo(newName);
        assertThat(captured.getEmail()).isEqualTo(newEmail);
        assertThat(captured.getAge()).isEqualTo(age);
    }

    @Test
    void updateCustomer_AndCustomerIdExistAndNameUpdated() {
        // Given
        long customerId = customerDto.getId();
        String newName = "newName";
        when(customerDao.findById(customerId)).thenReturn(Optional.of(customerDto));
        CustomerUpdateRequest request = new CustomerUpdateRequest(newName, null, null);
        // When
        underTest.updateCustomer(customerId, request);
        // Then
        ArgumentCaptor<CustomerDto> customerDtoArgumentCaptor = ArgumentCaptor.forClass(CustomerDto.class);

        verify(customerDao).updateCustomer(customerDtoArgumentCaptor.capture());
        CustomerDto captured = customerDtoArgumentCaptor.getValue();

        assertThat(captured).isNotNull();
        assertThat(captured.getId()).isEqualTo(customerId);
        assertThat(captured.getName()).isEqualTo(newName);
        assertThat(captured.getEmail()).isEqualTo(customerDto.getEmail());
        assertThat(captured.getAge()).isEqualTo(customerDto.getAge());
    }
    @Test
    void updateCustomer_AndCustomerIdExistAndAgeUpdated() {
        // Given
        long customerId = customerDto.getId();
        int age = 50;
        when(customerDao.findById(customerId)).thenReturn(Optional.of(customerDto));
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, age);
        // When
        underTest.updateCustomer(customerId, request);
        // Then
        ArgumentCaptor<CustomerDto> customerDtoArgumentCaptor = ArgumentCaptor.forClass(CustomerDto.class);

        verify(customerDao).updateCustomer(customerDtoArgumentCaptor.capture());
        CustomerDto captured = customerDtoArgumentCaptor.getValue();

        assertThat(captured).isNotNull();
        assertThat(captured.getId()).isEqualTo(customerId);
        assertThat(captured.getName()).isEqualTo(customerDto.getName());
        assertThat(captured.getEmail()).isEqualTo(customerDto.getEmail());
        assertThat(captured.getAge()).isEqualTo(age);
    }
    @Test
    void updateCustomer_AndCustomerIdExistAndEmailUpdated() {
        // Given
        long customerId = customerDto.getId();
        String newEmail = "newEmail@email.com";
        when(customerDao.findById(customerId)).thenReturn(Optional.of(customerDto));
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, newEmail, null);
        when(customerDao.existCustomerWithEmail(newEmail)).thenReturn(false);
        // When
        underTest.updateCustomer(customerId, request);
        // Then
        ArgumentCaptor<CustomerDto> customerDtoArgumentCaptor = ArgumentCaptor.forClass(CustomerDto.class);

        verify(customerDao).updateCustomer(customerDtoArgumentCaptor.capture());
        CustomerDto captured = customerDtoArgumentCaptor.getValue();

        assertThat(captured).isNotNull();
        assertThat(captured.getId()).isEqualTo(customerId);
        assertThat(captured.getName()).isEqualTo(customerDto.getName());
        assertThat(captured.getEmail()).isEqualTo(newEmail);
        assertThat(captured.getAge()).isEqualTo(customerDto.getAge());
    }
}