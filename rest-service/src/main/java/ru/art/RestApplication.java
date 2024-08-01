package ru.art;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "ru.art.entity")
@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.art.dao")
public class RestApplication {
    public static void main(String[] args) {SpringApplication.run(RestApplication.class);}
}
