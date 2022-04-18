package com.sendersqs.sender.model;

import lombok.Data;

@Data
public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private String created_at;
    private String updated_at;
}