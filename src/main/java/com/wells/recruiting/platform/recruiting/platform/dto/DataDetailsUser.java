package com.wells.recruiting.platform.recruiting.platform.dto;

import com.wells.recruiting.platform.recruiting.platform.user.User;

public record  DataDetailsUser(Long _id, String name, String email) {
    public DataDetailsUser(User user) {
        this(user.get_id(), user.getName(), user.getEmail());
    }
}