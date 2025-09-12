package com.wells.recruiting.platform.recruiting.platform.repository;

import com.wells.recruiting.platform.recruiting.platform.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
//    Page<User> findAllByActiveTrue(Pageable pageable);
    UserDetails findByEmail(String email);

    boolean existsByEmail(String email);

}