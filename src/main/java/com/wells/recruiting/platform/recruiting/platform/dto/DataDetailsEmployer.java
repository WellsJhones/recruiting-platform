package com.wells.recruiting.platform.recruiting.platform.dto;

import com.wells.recruiting.platform.recruiting.platform.company.Employer;

public record DataDetailsEmployer(
        Long _id,
        String name,
        String email,
        String role,
        String companyName,
        String companyDescription,
        String companyLogo
) {
    public DataDetailsEmployer(Employer employer) {
        this(
                employer.get_id(),
                employer.getName(),
                employer.getEmail(),
                employer.getRole(),
                employer.getCompanyName(),
                employer.getCompanyDescription(),
                employer.getCompanyLogo()
        );
    }
}
