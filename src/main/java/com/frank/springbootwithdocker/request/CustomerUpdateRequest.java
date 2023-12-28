package com.frank.springbootwithdocker.request;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
){
}
