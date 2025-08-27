package com.java.TrainningJV.common.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import com.java.TrainningJV.common.enums.StatsRange;

public class DateRangeUtil {
    public static LocalDate[] getRange(StatsRange range){
        LocalDate fromDate, toDate;
        LocalDate today = LocalDate.now();

        switch (range) {
            case TODAY:
                fromDate = today;
                toDate = today;
                break;

            case WEEK:
                fromDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                toDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;

            case MONTH:
                fromDate = today.withDayOfMonth(1);
                toDate = today.withDayOfMonth(today.lengthOfMonth());
                break;
            
            case YEAR:
                fromDate = today.withDayOfYear(1);
                toDate = today.withDayOfYear(today.lengthOfYear());
                break;
            default:
                throw new IllegalArgumentException("Unsupported fixed range: " + range);
        }

        return  new LocalDate[]{fromDate, toDate};
    }

    public static LocalDate[] getRangeCustom(LocalDate from, LocalDate to){
        if (from == null || to == null) {
            throw new IllegalArgumentException("Custom range requires both from and to date");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("FromDate must be before or equal to ToDate");
        }
        return new LocalDate[]{from, to};
    }
    
}
