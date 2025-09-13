package com.wells.recruiting.platform.recruiting.platform.dto;

import com.wells.recruiting.platform.recruiting.platform.user.User;

import java.time.Instant;

// Java
public record DataDetailsUser(
        String _id,
        String name,
        String email,
        String role,
        String avatar,
        Instant createdAt,
        Instant updatedAt,
        int __v,
        String resume
) {
    // Full details constructor
    public DataDetailsUser(User user) {
        this(
                String.valueOf(user.get_id()),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getAvatar(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getVersion(),
                user.getResume()
        );
    }

    // Minimal fields constructor
    public DataDetailsUser(String _id, String name, String email) {
        this(_id, name, email, null, null, null, null, 0, null);
    }
}
