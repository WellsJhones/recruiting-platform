package com.wells.recruiting.platform.recruiting.platform.dto;

public class EmployerFullDTO {
    private String _id;
    private String name;
    private String email;
    private String role;
    private String avatar;
    private String companyName;
    private String companyDescription;
    private String companyLogo;
    private String token;

    public EmployerFullDTO(String _id, String name, String email, String role, String avatar, String companyName, String companyDescription, String companyLogo, String token) {
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

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyDescription() { return companyDescription; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }

    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
