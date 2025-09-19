// Java
package com.wells.recruiting.platform.recruiting.platform.controler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import com.wells.recruiting.platform.recruiting.platform.dto.DataAuthentication;
import com.wells.recruiting.platform.recruiting.platform.dto.EmployerFullDTO;
import com.wells.recruiting.platform.recruiting.platform.dto.UserFullDTO;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.UserRepository;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import com.wells.recruiting.platform.recruiting.platform.service.EmailService;
import com.wells.recruiting.platform.recruiting.platform.user.User;

import jakarta.validation.Valid;

@CrossOrigin(origins = { "http://localhost:5173", "http://wellsjhones.com.br",
        "http://164.152.61.249", "https://wellsjhones.com.br" }, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid DataAuthentication dados) {
        User user = userRepository.findByEmail(dados.email());
        if (user != null) {
            if (!passwordEncoder.matches(dados.password(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String tokenJWT = tokenService.generateToken(user);
            UserFullDTO response = new UserFullDTO(
                    String.valueOf(user.get_id()),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getAvatar(),
                    user.getResume(),
                    tokenJWT);
            return ResponseEntity.ok(response);
        }

        Employer employer = employerRepository.findByEmail(dados.email());
        if (employer != null) {
            if (!passwordEncoder.matches(dados.password(), employer.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String tokenJWT = tokenService.generateToken(employer);
            EmployerFullDTO response = new EmployerFullDTO(
                    String.valueOf(employer.get_id()),
                    employer.getName(),
                    employer.getEmail(),
                    employer.getRole(),
                    employer.getAvatar(),
                    employer.getCompanyName(),
                    employer.getCompanyDescription(),
                    employer.getCompanyLogo(),
                    tokenJWT);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshJwt(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        DecodedJWT jwt;
        try {
            jwt = tokenService.verifyTokenIgnoreExpiration(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        String email = jwt.getSubject();
        String role = jwt.getClaim("role").asString();
        if (role == null || email == null) {
            return ResponseEntity.status(401).body("Invalid token claims");
        }
        if ("employer".equalsIgnoreCase(role)) {
            Employer employer = employerRepository.findByEmail(email);
            if (employer == null) {
                return ResponseEntity.status(401).body("Employer not found");
            }
            String newToken = tokenService.generateToken(employer);
            return ResponseEntity.ok(java.util.Map.of("token", newToken));
        } else if ("jobseeker".equalsIgnoreCase(role)) {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(401).body("User not found");
            }
            String newToken = tokenService.generateToken(user);
            return ResponseEntity.ok(java.util.Map.of("token", newToken));
        } else {
            return ResponseEntity.status(401).body("Unknown role");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> recoverPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        User user = userRepository.findByEmail(email);
        Employer employer = employerRepository.findByEmail(email);
        if (user == null && employer == null) {
            // For security, do not reveal if email exists
            return ResponseEntity.ok("If the email exists, a recovery link will be sent.");
        }
        // Generate a recovery token (for demo, use a UUID)
        String token = java.util.UUID.randomUUID().toString();
        // TODO: Save token and associate with user/employer, set expiration
        String recoveryLink = "https://164.152.61.249/reset-password?token=" + token;
        // Send recovery email
        emailService.sendPasswordResetEmail(email, recoveryLink);
        // Respond generically
        return ResponseEntity.ok("If the email exists, a recovery link will be sent.");
    }

}
