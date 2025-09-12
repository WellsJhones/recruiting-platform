package com.wells.recruiting.platform.recruiting.platform.dto;
// src/main/java/com/wells/recruiting/platform/recruiting/platform/dto/SignupRequest.java
public class SignupRequest {
    private String role;
    private String email;
    private String password;
    private String companyName;
    private String companyDescription;
    private String companyLogo;
    private String name;
    private String jobseekerName;

    // Getters
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getCompanyName() { return companyName; }
    public String getCompanyDescription() { return companyDescription; }
    public String getCompanyLogo() { return companyLogo; }
    public String getName() { return name; }
    public String getJobseekerName() { return jobseekerName; }

    // Setters (optional, needed for deserialization)
    public void setRole(String role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }
    public void setName(String name) { this.name = name; }
    public void setJobseekerName(String jobseekerName) { this.jobseekerName = jobseekerName; }
}
