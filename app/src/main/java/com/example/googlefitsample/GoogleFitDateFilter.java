package com.example.googlefitsample;

import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * GoogleFit에서 데이트를 조회하고 싶은 기간 설정
 * 시작 년/월/일,    끝 년/월/일 로 조회
 */
public class GoogleFitDateFilter {

    private Long startTime;
    private Long endTime;
    private String startString;
    private String endString;

    private static final String TAG = "GoogleFitDateFilter";

    public GoogleFitDateFilter(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay, boolean currentTime) {

        final Calendar cal = Calendar.getInstance();
        final Calendar endCal = Calendar.getInstance();

        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);
        endCal.setTime(now);


        //Calendar의 Month는 현재 달보다 1개월 적음 그래서 입력받은 월이 11월이라면 -1 해줘야 함
        cal.set(cal.get(Calendar.YEAR), startMonth - 1, startDay, 0, 0, 0);
        startTime = cal.getTimeInMillis();

        int endHour = 0;
        int endMinute = 0;
        if (currentTime) {
            endHour = endCal.get(Calendar.HOUR_OF_DAY);
            endMinute = endCal.get(Calendar.MINUTE);
        } else {
            endHour = 24;
            endMinute = 24;
        }

        endCal.set(endCal.get(Calendar.YEAR), endMonth - 1, endDay, endHour, endMinute, 0);
        endTime = endCal.getTimeInMillis();


        startString = startYear + "년 " + startMonth + "월 " + startDay
                + "일 " + "00시 " + "00분";

        endString = endYear + "년 " + endMonth + "월 " + endDay
                + "일 " + endHour + "시 " + endMinute + "분";

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
