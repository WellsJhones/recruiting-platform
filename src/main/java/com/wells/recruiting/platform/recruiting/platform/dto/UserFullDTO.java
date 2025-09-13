package com.wells.recruiting.platform.recruiting.platform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFullDTO {
    private String _id;
    private String name;
    private String email;
    private String role;
    private String avatar;
    private String resume;
    private String token;
    // Add other fields as needed

    public UserFullDTO(String _id, String name, String email, String role, String avatar, String resume, String token) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
        this.resume = resume;
        this.token = token;
    }
    // Getters and setters
}
