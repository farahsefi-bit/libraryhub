package com.sip.libraryhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LibraryhubApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryhubApplication.class, args);
    }
}