package com.example.t_app;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class TAppApplication {


    public static void main(String[] args) {

        SpringApplication.run(TAppApplication.class, args);
    }


}


