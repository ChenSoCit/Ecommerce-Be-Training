package com.java.TrainningJV.dtos.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserPageResponse extends PageResponseAbstract {
    private List<UserPagingResponse> users;
}
