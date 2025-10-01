package com.dorsetsoftware.PennyPal.user.dto;

public class UserProfileUpdateDto {
    private String username;
    private String profilePictureUrl;

    public UserProfileUpdateDto() {
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
