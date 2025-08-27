package com.java.TrainningJV.dtos.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull(message = "user Id must be not null")
    private Integer userId;

    @NotNull(message = "full name must be not null")
    private String fullName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "phone must be not null")
    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "phone number is invalid")
    private String phone;

    @NotNull(message = "address must be not null")
    private String address;

    private String status; 
}
