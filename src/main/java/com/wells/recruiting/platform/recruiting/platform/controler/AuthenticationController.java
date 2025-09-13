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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // or specify allowed origins
@RestController
@RequestMapping("/api/auth/login")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployerRepository employerRepository;


    // Java
    @PostMapping
    public ResponseEntity login(@RequestBody @Valid DataAuthentication dados) {
        User user = userRepository.findByEmail(dados.email());
        if (user != null) {
            if (!passwordEncoder.matches(dados.password(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String tokenJWT = tokenService.generateToken(user);
            DataLoginResponse response = new DataLoginResponse(
                    user.get_id(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    tokenJWT
            );
            return ResponseEntity.ok(response);
        }

        Employer employer = employerRepository.findByEmail(dados.email());
        if (employer != null) {
            if (!passwordEncoder.matches(dados.password(), employer.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String tokenJWT = tokenService.generateToken(employer);
            DataLoginResponseEmployer response = new DataLoginResponseEmployer(
                    employer.get_id(),
                    employer.getName(),
                    employer.getEmail(),
                    employer.getRole(),
                    employer.getCompanyName(),
                    employer.getCompanyDescription(),
                    employer.getCompanyLogo(),
                    tokenJWT
            );
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
