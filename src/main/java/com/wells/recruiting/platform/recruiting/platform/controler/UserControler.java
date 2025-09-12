package com.wells.recruiting.platform.recruiting.platform.controler;


import com.wells.recruiting.platform.recruiting.platform.dto.DataDetailsUser;
import com.wells.recruiting.platform.recruiting.platform.dto.DataLoginResponse;
import com.wells.recruiting.platform.recruiting.platform.dto.UserCreateData;
import com.wells.recruiting.platform.recruiting.platform.repository.UserRepository;
import com.wells.recruiting.platform.recruiting.platform.security.TokenService;
import com.wells.recruiting.platform.recruiting.platform.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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



    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Object> registerUser(@RequestBody @Valid UserCreateData data, UriComponentsBuilder uriBuilder) {
        if (repository.existsByEmail(data.email())) {
            return ResponseEntity.status(409).body("Email already in use");
        }
        String hashedPassword = passwordEncoder.encode(data.password());
        User createUser = new User(data);
        createUser.setPassword(hashedPassword);
        repository.save(createUser);

        // Generate JWT token (replace with your actual token generation logic)
        String tokenJWT = tokenService.generateToken(createUser); // You need to implement jwtService

        DataLoginResponse response = new DataLoginResponse(
                createUser.get_id(),
                createUser.getName(),
                createUser.getEmail(),
                createUser.getRole(),
                tokenJWT
        );
        return ResponseEntity.ok(response);
    }


}
