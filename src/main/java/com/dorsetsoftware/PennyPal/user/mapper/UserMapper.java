package com.dorsetsoftware.PennyPal.user.mapper;

import com.dorsetsoftware.PennyPal.user.dto.UserDto;
import com.dorsetsoftware.PennyPal.user.entity.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setAlias(user.getAlias());

        return dto;
    }
}
