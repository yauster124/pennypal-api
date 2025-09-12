package com.dorsetsoftware.PennyPal.user.dto;

public class UserDto {
    private Long id;
    private String username;

    public UserDto() {}

    public UserDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return this.username;
    }
}
