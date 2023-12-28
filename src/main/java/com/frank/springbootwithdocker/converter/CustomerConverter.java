package com.frank.springbootwithdocker.converter;

import com.frank.springbootwithdocker.entity.Customer;
import com.frank.springbootwithdocker.dto.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerConverter {

    private final ModelMapper modelMapper;

    public CustomerDto toDTO(Customer entity) {
        return modelMapper.map(entity, CustomerDto.class);
    }

    public Customer toEntity(CustomerDto dto) {
        return modelMapper.map(dto, Customer.class);
    }
}
