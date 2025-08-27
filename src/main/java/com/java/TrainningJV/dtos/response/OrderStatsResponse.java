package com.java.TrainningJV.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatsResponse {
    private String range;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int totalOrder;
    private BigDecimal totalMoney;

}
