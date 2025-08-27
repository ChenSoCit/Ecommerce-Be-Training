package com.java.TrainningJV.dtos.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserResponse {
    @JsonIgnore
    private int userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String address;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonIgnore
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonIgnore
    private Date updatedAt;

    private List<OrderResponse> orders;
}
