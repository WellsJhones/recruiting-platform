package com.wells.recruiting.platform.recruiting.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wells.recruiting.platform.recruiting.platform.company.*;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    boolean existsByEmail(String email);

    Employer findByEmail(String email);
}