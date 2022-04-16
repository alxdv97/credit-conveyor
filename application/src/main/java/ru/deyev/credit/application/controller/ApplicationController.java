package ru.deyev.credit.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {
    @GetMapping("/hello")
    public ResponseEntity<String> getTestString(){
        return ResponseEntity.ok("Hello from Application microsevice");
    }
}
