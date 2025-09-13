package com.wells.recruiting.platform.recruiting.platform.controler;

import com.wells.recruiting.platform.recruiting.platform.company.Employer;
import com.wells.recruiting.platform.recruiting.platform.dto.*;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;
import com.wells.recruiting.platform.recruiting.platform.repository.UserRepository;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import com.wells.recruiting.platform.recruiting.platform.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
    public ResponseEntity<Object> registerUser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "companyDescription", required = false) String companyDescription,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        if (repository.existsByEmail(email) || employerRepository.existsByEmail(email)) {
            return ResponseEntity.status(409).body("Email already in use");
        }

        if (role == null || email == null || password == null || name == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            String uploadDir = "C:\\Users\\Wells\\Documents\\uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String fileName = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            File dest = new File(dir, fileName);
            try {
                image.transferTo(dest);
                String imageUrl = "http://localhost:8000/uploads/" + fileName;
                imagePath = imageUrl; // <-- Assign the URL to imagePath
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Image upload failed");
            }

        }

        if ("employer".equalsIgnoreCase(role)) {
            Employer employer = new Employer();
            employer.setCompanyName(companyName);
            employer.setCompanyDescription(companyDescription);
            employer.setAvatar(imagePath);
            employer.setCompanyLogo(imagePath);
            employer.setName(name);
            employer.setEmail(email);
            employer.setPassword(passwordEncoder.encode(password));
            employer.setRole(role);
            employerRepository.save(employer);

            String tokenJWT = tokenService.generateToken(employer);
            DataLoginResponseEmployer response = new DataLoginResponseEmployer(
                    employer.get_id(),
                    employer.getName(),
                    employer.getEmail(),
                    employer.getRole(),
                    employer.getAvatar(),
                    employer.getCompanyName(),
                    employer.getCompanyDescription(),
                    employer.getCompanyLogo(),
                    tokenJWT
            );
            return ResponseEntity.ok(response);
        } else if ("jobseeker".equalsIgnoreCase(role)) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setAvatar(imagePath); // <-- Set avatar path
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

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.extractEmail(token);
        String role = tokenService.extractRole(token);

        if ("employer".equalsIgnoreCase(role)) {
            Employer employer = employerRepository.findByEmail(email);
            if (employer != null) {
                DataDetailsEmployer response = new DataDetailsEmployer(
                        employer.get_id(),
                        employer.getName(),
                        employer.getEmail(),
                        employer.getRole(),
                        employer.getCompanyName(),
                        employer.getCompanyDescription(),
                        employer.getCompanyLogo(),
                        employer.getAvatar()

                );
                return ResponseEntity.ok(response);
            }
        } else if ("jobseeker".equalsIgnoreCase(role)) {
            User user = repository.findByEmail(email);
            if (user != null) {
                DataDetailsUser response = new DataDetailsUser(user);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
