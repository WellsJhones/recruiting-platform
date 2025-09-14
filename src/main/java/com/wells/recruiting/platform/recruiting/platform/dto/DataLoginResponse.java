// Java
package com.wells.recruiting.platform.recruiting.platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataLoginResponse {
    @JsonProperty("_id")
    private Long _id;
    private String name;
    private String avatar;
    private String email;
    private String role;
    private String token;


    public DataLoginResponse(Long _id,String avatar ,String name, String email, String role, String token) {
        this._id = _id;
        this.name = name;
        this.avatar = avatar;
        this.email = email;
        this.role = role;
        this.token = token;
    }

}
