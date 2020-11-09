package com.example.googlefitsample;

import java.util.Calendar;
import java.util.Date;


/**
 * 당일 0시 ~ 현재시간 조회
 */
public class GoogleFitTodayData {

    private Long startTime;
    private Long endTime;
    private String startString;
    private String endString;

    public GoogleFitTodayData() {
        final Calendar cal = Calendar.getInstance();
        final Calendar endCal = Calendar.getInstance();

        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);
        endCal.setTime(now);

        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        startTime = cal.getTimeInMillis();


        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), endCal.get(Calendar.HOUR_OF_DAY),
                endCal.get(Calendar.MINUTE), 0);
        endTime = cal.getTimeInMillis();


        startString = cal.get(Calendar.YEAR) + "년 " + cal.get(Calendar.MONTH) + "월 " + cal.get(Calendar.DAY_OF_MONTH)
                + "일 " + "00시 " + " 00분";


        endString = endCal.get(Calendar.YEAR) + "년 " + endCal.get(Calendar.MONTH) + "월 " + endCal.get(Calendar.DAY_OF_MONTH)
                + "일 " + endCal.get(Calendar.HOUR_OF_DAY) + "시 " + endCal.get(Calendar.MINUTE) + "분";

    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public String getStartString() {
        return startString;
    }

    public String getEndString() {
        return endString;
    }
}
