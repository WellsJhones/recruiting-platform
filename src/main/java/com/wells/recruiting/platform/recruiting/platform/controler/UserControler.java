// Java
package com.wells.recruiting.platform.recruiting.platform.controler;

import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import com.wells.recruiting.platform.recruiting.platform.dto.*;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.UserRepository;
import com.wells.recruiting.platform.recruiting.platform.security.DataTokenJWT;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import com.wells.recruiting.platform.recruiting.platform.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class UserControler {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmployerRepository employerRepository;



    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Object> registerUser(@RequestBody @Valid SignupRequest data) {
        String role = data.getRole();
        String email = data.getEmail();
        String password = data.getPassword();
        if (repository.existsByEmail(email) || employerRepository.existsByEmail(email)) {
            return ResponseEntity.status(409).body("Email already in use");
        }

        if (role == null || email == null || password == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        if (repository.existsByEmail(email)) {
            return ResponseEntity.status(409).body("Email already in use");
        }

        if ("employer".equalsIgnoreCase(role)) {
            if (data.getName() == null) {
                return ResponseEntity.badRequest().body("Missing employer fields");
            }
            Employer employer = new Employer();
            employer.setCompanyName(data.getCompanyName());
            employer.setCompanyDescription(data.getCompanyDescription());
            employer.setCompanyLogo(data.getCompanyLogo());
            employer.setName(data.getName());
            employer.setEmail(email);
            employer.setPassword(passwordEncoder.encode(password));
            employer.setRole(role);
            employerRepository.save(employer);

            String companyName = employer.getCompanyName() != null ? employer.getCompanyName() : "Unknown Company";
            String companyDescription = employer.getCompanyDescription() != null ? employer.getCompanyDescription() : "";
            String companyLogo = employer.getCompanyLogo() != null ? employer.getCompanyLogo() : "";

            String tokenJWT = tokenService.generateToken(employer);
            DataLoginResponseEmployer response = new DataLoginResponseEmployer(
                    employer.get_id(),
                    employer.getName(),
                    employer.getEmail(),
                    employer.getRole(),
                    companyName,
                    companyDescription,
                    companyLogo,
                    tokenJWT
            );

            return ResponseEntity.ok(response);
        } else if ("jobseeker".equalsIgnoreCase(role)) {
            if (data.getName() == null) {
                return ResponseEntity.badRequest().body("Missing jobseeker fields");
            }
            User user = new User();
            user.setName(data.getName());
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            repository.save(user);
            String tokenJWT = tokenService.generateToken(user);
            DataLoginResponse response = new DataLoginResponse(
                    user.get_id(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    tokenJWT
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Invalid role");
        }
    }

    // Java
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.extractEmail(token);
        String role = tokenService.extractRole(token); // Add this method to TokenService

        System.out.println("Extracted email: " + email);
        System.out.println("Extracted role: " + role);

        if ("employer".equalsIgnoreCase(role)) {
            Employer employer = employerRepository.findByEmail(email);
            System.out.println("Employer found: " + (employer != null));
            if (employer != null) {
                DataDetailsEmployer response = new DataDetailsEmployer(
                        employer.get_id(),
                        employer.getName(),
                        employer.getEmail(),
                        employer.getRole(),
                        employer.getCompanyName(),
                        employer.getCompanyDescription(),
                        employer.getCompanyLogo()
                );
                return ResponseEntity.ok(response);
            }
        } else if ("jobseeker".equalsIgnoreCase(role)) {
            User user = repository.findByEmail(email);
            System.out.println("User found: " + (user != null));
            if (user != null) {
                DataDetailsUser response = new DataDetailsUser(user);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
