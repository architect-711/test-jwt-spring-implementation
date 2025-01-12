package test.architect_711.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/people")
public class PersonController {
    @GetMapping("/username")
    public ResponseEntity<?> personUsername() {
        String username = String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(username);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello everyone!");
    }
}
