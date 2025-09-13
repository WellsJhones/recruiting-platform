package com.wells.recruiting.platform.recruiting.platform.company;

import com.wells.recruiting.platform.recruiting.platform.dto.EmployerDataCreate;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "employer")
@Entity(name = "employer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "_id")
public class Employer implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long _id;
    private String name;
    private String email;
    private String password;
    private String avatar;
    private boolean active = true;
    private String role;
    private String companyName;
    private String companyDescription;
    private String companyLogo;

    public Employer(EmployerDataCreate data) {
        this.active = true;
        this.name = data.name();
        this.email = data.email();
        this.password = data.password();
        this.avatar = data.avatar() != null ? data.avatar() : "";
        this.role = data.role();
        this.companyName = data.companyName() != null ? data.companyName() : "Unknown Company";
        this.companyDescription = data.companyDescription() != null ? data.companyDescription() : "";
        this.companyLogo = data.companyLogo() != null ? data.companyLogo() : "";
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + _id +
                ", nome='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", active=" + active +
                ", role='" + role + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyDescription='" + companyDescription + '\'' +
                ", companyLogo='" + companyLogo + '\'' +
                '}';
    }

    public Long getId() {
        return _id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
