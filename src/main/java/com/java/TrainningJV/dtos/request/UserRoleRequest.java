package com.java.TrainningJV.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleRequest {
     @NotNull(message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "password must be not null")
    private String password;

    @NotNull(message = "Date of birth is required")
    private String dateOfBirth;

    @NotNull(message = "Gender is required")
    private String gender;

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Address is required")
    private String address;
    
    @NotNull(message = "Role name is required")
    private String  roleName; 

    private int roleId;
}