package com.wells.recruiting.platform.recruiting.platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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


}
