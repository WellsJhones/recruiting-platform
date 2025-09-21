package com.wells.recruiting.platform.recruiting.platform.controler;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.wells.recruiting.platform.recruiting.platform.repository.UserRepository;
import com.wells.recruiting.platform.recruiting.platform.user.User;
import com.wells.recruiting.platform.recruiting.platform.repository.EmployerRepository;
import com.wells.recruiting.platform.recruiting.platform.company.Employer;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = { "http://localhost:5173", "http://wellsjhones.com.br", "http://164.152.61.249",
        "https://wellsjhones.com.br" }, allowCredentials = "true")
public class AdmController {

    // Disable a user
    @PutMapping("/users/{id}/disable")
    public User disableUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(false);
        return userRepository.save(user);
    }

    // Enable a user
    @PutMapping("/users/{id}/enable")
    public User enableUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(true);
        return userRepository.save(user);
    }

    // Get platform statistics
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("users", userRepository.count());
        stats.put("companies", employerRepository.count());
        return stats;
    }

    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployerRepository employerRepository;

    // Return all users and employers for frontend
    @GetMapping("/all-accounts")
    public Map<String, Object> getAllAccounts() {
        Map<String, Object> result = new HashMap<>();
        result.put("users", userRepository.findAll());
        result.put("employers", employerRepository.findAll());
        return result;
    }

    // List all jobseekers
    @GetMapping("/jobseekers")
    public List<User> getAllJobseekers() {
        return userRepository.findAll();
    }

    // List all employers
    @GetMapping("/employers")
    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    // Activate/deactivate a jobseeker
    @PutMapping("/jobseekers/{id}/active")
    public User setJobseekerActive(@PathVariable Long id, @RequestParam boolean active) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(active);
        return userRepository.save(user);
    }

    // Activate/deactivate an employer
    @PutMapping("/employers/{id}/active")
    public Employer setEmployerActive(@PathVariable Long id, @RequestParam boolean active) {
        Employer employer = employerRepository.findById(id).orElseThrow();
        employer.setActive(active);
        return employerRepository.save(employer);
    }

    // Delete a jobseeker
    @DeleteMapping("/jobseekers/{id}")
    public void deleteJobseeker(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    // Delete an employer
    @DeleteMapping("/employers/{id}")
    public void deleteEmployer(@PathVariable Long id) {
        employerRepository.deleteById(id);
    }
}
