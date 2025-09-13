package com.wells.recruiting.platform.recruiting.platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataLoginResponseEmployer {
    @JsonProperty("_id")
    private Long _id;
    private String name;
    private String email;
    private String role;
    private String companyName;
    private String companyDescription;
    private String companyLogo;
    private String token;
    private String avatar;

    public DataLoginResponseEmployer(Long _id, String name, String email, String role, String avatar,
                                     String companyName, String companyDescription, String companyLogo, String token) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
        this.companyName = companyName;
        this.companyDescription = companyDescription;
        this.companyLogo = companyLogo;
        this.token = token;
    }

    // Getters and setters
    public Long get_id() { return _id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public String getCompanyName() { return companyName; }
    public String getCompanyDescription() { return companyDescription; }
    public String getCompanyLogo() { return companyLogo; }
}
