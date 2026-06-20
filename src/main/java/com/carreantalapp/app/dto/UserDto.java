package com.carreantalapp.app.dto;

import com.carreantalapp.app.model.utils.UserRoleTypes;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UserDto {
    private final Long id;
    private final String username;
    private final boolean enabled;
    private final UserRoleTypes role;
    private final String companyName;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String cnp;
    private final String email;
}
