package com.wells.recruiting.platform.recruiting.platform.user;

import com.wells.recruiting.platform.recruiting.platform.dto.UserCreateData;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Table(name = "user")
@Entity(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "_id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long _id;
    private String name;
    private String email;
    private String password;
    private String avatar;
    private boolean active = true;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;
    private int version;
    private String resume;


    public User(UserCreateData data) {
        this.active = true;
        this.name = data.name();
        this.email = data.email();
        this.password = data.password();
        this.avatar = "";
        this.role = data.role();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.version = 0;
        this.resume = "";
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

