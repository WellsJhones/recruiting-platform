// Java
package com.wells.recruiting.platform.recruiting.platform.controler;

import com.wells.recruiting.platform.recruiting.platform.dto.DataAuthentication;
import com.wells.recruiting.platform.recruiting.platform.dto.DataDetailsUser;
import com.wells.recruiting.platform.recruiting.platform.dto.DataLoginResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

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

    // Java
    @PostMapping
    public ResponseEntity login(@RequestBody @Valid DataAuthentication dados) {
        UserDetails userDetails = userRepository.findByEmail(dados.email());
        if (userDetails == null || !(userDetails instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = (User) userDetails;
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

}
