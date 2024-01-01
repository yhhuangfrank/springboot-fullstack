package com.frank.springbootwithdocker.request;

public record CustomerRegisterRequest (
        String name,
        String email,
        Integer age
){
}
