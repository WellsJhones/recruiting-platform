// Java
package com.wells.recruiting.platform.recruiting.platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataLoginResponse {
    @JsonProperty("_id")
    private Long _id;
    private String name;
    private String email;
    private String role;
    private String token;

    public DataLoginResponse(Long _id, String name, String email, String role, String token) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    // Getters and setters
    public Long get_id() { return _id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
}
