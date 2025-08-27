package com.java.TrainningJV.dtos.response;

import java.util.Date;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserPagingResponse {
    private int userId;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private int roleId;
    private Date dateOfBirth;
    private String password;
    private Date createdAt;
    private Date updatedAt;

}
