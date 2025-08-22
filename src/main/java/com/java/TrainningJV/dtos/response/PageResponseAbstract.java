package com.java.TrainningJV.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class PageResponseAbstract {
    private int pageNumber;
    private int pageSize;
    private long totalPages;
    private long totalElements;

}
