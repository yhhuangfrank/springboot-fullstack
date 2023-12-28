package com.frank.springbootwithdocker.dto;

import com.frank.springbootwithdocker.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link Customer}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Integer id;
    private String name;
    private String email;
    private Integer age;
}