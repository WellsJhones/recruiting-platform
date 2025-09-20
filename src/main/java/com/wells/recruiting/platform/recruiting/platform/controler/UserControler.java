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
import java.util.Map;

@CrossOrigin(origins = { "http://localhost:5173", "http://wellsjhones.com.br", "http://164.152.61.249",
        "https://wellsjhones.com.br" }, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class UserControler {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmployerRepository employerRepository;

    @PostMapping("/auth/register")
    @Transactional
    public ResponseEntity<Object> registerUser(
            @RequestBody Map<String, Object> payload,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        String name = (String) payload.get("name");
        String email = (String) payload.get("email");
        String password = (String) payload.get("password");
        String role = (String) payload.get("role");
        String companyName = (String) payload.get("companyName");
        String companyDescription = (String) payload.get("companyDescription");

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
            if (!dir.exists() && !dir.mkdirs()) {
                return ResponseEntity.status(500).body("Failed to create upload directory");
            }
            // Sanitize the original filename
            String originalName = image.getOriginalFilename();
            String sanitized = originalName == null ? "file"
                    : originalName
                            .toLowerCase()
                            .replaceAll("\\s+", "_")
                            .replaceAll("[^a-z0-9._-]", "");
            String fileName = System.currentTimeMillis() + "-" + sanitized;
            File dest = new File(dir, fileName);

            try {
                image.transferTo(dest);
                String imageUrl = "http://localhost:8000/uploads/" + fileName;
                imagePath = imageUrl;
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Image upload failed");
            }
        } else if (payload.get("avatar") != null) {
            imagePath = (String) payload.get("avatar");
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
                    tokenJWT);
            return ResponseEntity.ok(response);
        } else if ("jobseeker".equalsIgnoreCase(role)) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setAvatar(imagePath);
            repository.save(user);

            String tokenJWT = tokenService.generateToken(user);
            DataLoginResponse response = new DataLoginResponse(
                    user.get_id(),
                    user.getAvatar(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    tokenJWT);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Invalid role");
        }
    }

    @GetMapping("/auth/me")
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

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getPublicUserInfo(@PathVariable("id") String id) {
        try {
            Long longId = Long.parseLong(id);
            User user = repository.findById(longId).orElse(null);
            if (user != null) {
                DataDetailsUser response = new DataDetailsUser(user);
                return ResponseEntity.ok(response);
            }
            Employer employer = employerRepository.findById(longId).orElse(null);
            if (employer != null) {
                DataDetailsEmployer response = new DataDetailsEmployer(
                        employer.get_id(),
                        employer.getName(),
                        employer.getEmail(),
                        employer.getRole(),
                        employer.getCompanyName(),
                        employer.getCompanyDescription(),
                        employer.getCompanyLogo(),
                        employer.getAvatar());
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid user ID format");
        }
    }

    // In
    // src/main/java/com/wells/recruiting/platform/recruiting/platform/controler/UserControler.java

    // JSON version
    @PutMapping(value = "/user/profile", consumes = { "application/json" })
    @Transactional
    public ResponseEntity<?> updateUserProfileJson(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> payload) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.extractEmail(token);
        String role = tokenService.extractRole(token);

        if ("employer".equalsIgnoreCase(role)) {
            Employer employer = employerRepository.findByEmail(email);
            if (employer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            employer.setCompanyName((String) payload.get("companyName"));
            employer.setCompanyDescription((String) payload.get("companyDescription"));
            employer.setCompanyLogo((String) payload.get("companyLogo"));
            employer.setAvatar((String) payload.get("avatar"));
            employer.setName((String) payload.get("name"));
            employer.setUpdatedAt(new java.util.Date());
            employerRepository.save(employer);
            return ResponseEntity.ok(new DataDetailsEmployer(
                    employer.get_id(),
                    employer.getName(),
                    employer.getEmail(),
                    employer.getRole(),
                    employer.getCompanyName(),
                    employer.getCompanyDescription(),
                    employer.getCompanyLogo(),
                    employer.getAvatar()));
        } else if ("jobseeker".equalsIgnoreCase(role)) {
            User user = repository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            user.setName((String) payload.get("name"));
            user.setAvatar((String) payload.get("avatar"));
            user.setResume((String) payload.get("resume"));
            user.setUpdatedAt(java.time.Instant.now());
            user.setVersion(user.getVersion() == null ? 1 : user.getVersion() + 1);
            repository.save(user);
            return ResponseEntity.ok(new DataDetailsUser(user));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
    }

    // Multipart version
    @PutMapping(value = "/user/profile", consumes = { "multipart/form-data" })
    @Transactional
    public ResponseEntity<?> updateUserProfileMultipart(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart(value = "data", required = false) Map<String, Object> payload,
            @RequestPart(value = "resume", required = false) MultipartFile resumeFile) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.extractEmail(token);
        String role = tokenService.extractRole(token);

        if ("employer".equalsIgnoreCase(role)) {
            Employer employer = employerRepository.findByEmail(email);
            if (employer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (payload != null) {
                employer.setCompanyName((String) payload.get("companyName"));
                employer.setCompanyDescription((String) payload.get("companyDescription"));
                employer.setCompanyLogo((String) payload.get("companyLogo"));
                employer.setAvatar((String) payload.get("avatar"));
                employer.setName((String) payload.get("name"));
            }
            employer.setUpdatedAt(new java.util.Date());
            employerRepository.save(employer);
            return ResponseEntity.ok(new DataDetailsEmployer(
                    employer.get_id(),
                    employer.getName(),
                    employer.getEmail(),
                    employer.getRole(),
                    employer.getCompanyName(),
                    employer.getCompanyDescription(),
                    employer.getCompanyLogo(),
                    employer.getAvatar()));
        } else if ("jobseeker".equalsIgnoreCase(role)) {
            User user = repository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (payload != null) {
                user.setName((String) payload.get("name"));
                user.setAvatar((String) payload.get("avatar"));
            }
            String resumeUrl = null;
            if (resumeFile != null && !resumeFile.isEmpty()) {
                String contentType = resumeFile.getContentType();
                if (contentType == null ||
                        !(contentType.equals("application/pdf") ||
                                contentType.equals("application/msword") ||
                                contentType.equals(
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
                    return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid resume file type"));
                }
                String uploadDir = "C:\\Users\\Wells\\Documents\\uploads\\resume";
                File dir = new File(uploadDir);
                if (!dir.exists() && !dir.mkdirs()) {
                    return ResponseEntity.status(500)
                            .body(java.util.Map.of("error", "Failed to create upload directory"));
                }
                String originalName = resumeFile.getOriginalFilename();
                String sanitized = originalName == null ? "resume"
                        : originalName
                                .toLowerCase()
                                .replaceAll("\\s+", "_")
                                .replaceAll("[^a-z0-9._-]", "");
                String fileName = System.currentTimeMillis() + "-" + sanitized;
                File dest = new File(dir, fileName);
                try {
                    resumeFile.transferTo(dest);
                    resumeUrl = "http://localhost:8000/uploads/" + fileName;
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(500).body(java.util.Map.of("error", "Resume upload failed"));
                }
            } else if (payload != null && payload.get("resume") != null) {
                resumeUrl = (String) payload.get("resume");
            }
            user.setResume(resumeUrl);
            user.setUpdatedAt(java.time.Instant.now());
            user.setVersion(user.getVersion() == null ? 1 : user.getVersion() + 1);
            repository.save(user);
            return ResponseEntity.ok(new DataDetailsUser(user));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
    }

    @PostMapping("/user/resume")
    @Transactional
    public ResponseEntity<?> deleteResumePost(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenService.extractEmail(token);
        String role = tokenService.extractRole(token);
        if (!"jobseeker".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only jobseekers can delete resume");
        }
        User user = repository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        user.setResume(null);
        user.setUpdatedAt(java.time.Instant.now());
        repository.save(user);
        return ResponseEntity.ok(java.util.Map.of("message", "Resume deleted"));
    }
}