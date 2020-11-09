package com.example.googlefitsample;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Utils {



    /**
     * 텍스트 숫자 올라가는 애니메이션 구현
     *
     * @param textView
     * @param strData
     * @param duration
     */
    static void numberCountUp(TextView textView, String strData, long duration) {
        Handler handler = new Handler();

        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {

                long maxNum = Long.parseLong(strData.replaceAll(",", "").replaceAll(" ", "").replaceAll("원", ""));
                boolean loopEnd = false;
                long increaseMount = maxNum / duration;
                long status = 0;
                while (!loopEnd) {
                    status += increaseMount;
                    if (status > maxNum) {
                        status = maxNum;
                        loopEnd = true;
                    }

                    long finalStatus = status;
                    handler.post(() -> {
                        textView.setText(Utils.GetNumFormatWon(finalStatus) + "걸음");
                    });

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    public static String GetNumFormatWon(double num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }
}
