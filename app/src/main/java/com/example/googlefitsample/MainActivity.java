package com.example.googlefitsample;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 200;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 300;
    private GoogleSignInAccount account;

    private static final int FILTER_TYPE_DAY = 1;
    private static final int FILTER_TYPE_MONTH = 2;
    private static final int FILTER_TYPE_YEAR = 3;
    private int day_steps = 0;
    private int month_steps = 0;
    FitnessOptions fitnessOptions;
    private int day_distance = 0;
    private int month_distance = 0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestGoogleFitPermission();
        findViewById(R.id.button2).setOnClickListener(v -> requestGoogleFit());

        findViewById(R.id.button).setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted

                Log.d(TAG, "onCreate: 퍼미션 없음");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, GOOGLE_FIT_PERMISSIONS_REQUEST_CODE);


            } else {
                //퍼미션 있음

                Log.d(TAG, "onCreate: 퍼미션 있음");
                accessGoogleFit();

                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("798744278992-9el4fkv93jqvbcc0qb7s2jj7ltkmr49b.apps.googleusercontent.com")
//                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_FIT_PERMISSIONS_REQUEST_CODE);
            }

        });


    }


    private void requestGoogleFitPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "onCreate: 퍼미션 없음");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, GOOGLE_FIT_PERMISSIONS_REQUEST_CODE);


        } else {
            // Configure Google Sign In
            //requestIdToken 얻을 때 약간 문제가 있었음
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("798744278992-9el4fkv93jqvbcc0qb7s2jj7ltkmr49b.apps.googleusercontent.com")
//                .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_FIT_PERMISSIONS_REQUEST_CODE);
        }

        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();


        account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);
        Log.d(TAG, "reqeustGoogleFitPermission: getEmail : " + account.getEmail());
        Log.d(TAG, "reqeustGoogleFitPermission: getId : " + account.getId());
        Log.d(TAG, "reqeustGoogleFitPermission: getIdToken : " + account.getIdToken());

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            Log.d(TAG, "reqeustGoogleFitPermission: 첫번재 조건문 통과");
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                    account,
                    fitnessOptions);
        } else {
            Log.d(TAG, "setGoogleFit: 퍼미션 이미 승인함");

//            accessGoogleFit();

        }
    }

    /**
     * 구글 계정 등록 요청
     */
    private void accessGoogleFit() {

//        account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        //구글 핏 활성화 여부 조회
        Fitness.getRecordingClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(aVoid -> {
                            Log.i(TAG, "Subscription was successful!");
                            requestGoogleFit();
                        }
                ).addOnFailureListener(e -> Log.i(TAG, "There was a problem subscribing: " + e.getLocalizedMessage()));
    }


    /**
     * 클라이언트가 동의한 구글 계정으로 데이터 요청
     */
    @SuppressLint("SetTextI18n")
    private void requestGoogleFit() {
        Log.d(TAG, "RequestGoogleFit: 진입");

        GoogleFitTodayData todayData = new GoogleFitTodayData();

        ((TextView) findViewById(R.id.startTime_text)).setText(todayData.getStartString());
        ((TextView) findViewById(R.id.endTime_text)).setText(todayData.getEndString());

        //클라이언트의 데이터 호출 = 당일 걸음수
        Fitness.getHistoryClient(this, account)
                .readData(setRequestDataStep(todayData.getStartTime(), todayData.getEndTime()))
                .addOnSuccessListener(response -> {

                    day_steps = 0;
                    for (Bucket bucket : response.getBuckets()) {
                        for (DataSet data : bucket.getDataSets()) {
                            dumpDataSet(data, FILTER_TYPE_DAY);
                        }
                    }

                    Utils.numberCountUp(findViewById(R.id.steps_textView), String.valueOf(day_steps), 1000);

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "accessGoogleFit: Failure : " + e);
                });


        GoogleFitDateFilter googleFitDateFilter = new GoogleFitDateFilter(
                2020, 11, 1, 2020, 11, 9, true);

        //클라이언트의 데이터 호출 이번달 걸음수
        Fitness.getHistoryClient(this, account)
                .readData(setRequestDataStep(googleFitDateFilter.getStartTime(), googleFitDateFilter.getEndTime()))
                .addOnSuccessListener(response -> {
                    month_steps = 0;

                    for (Bucket bucket : response.getBuckets()) {
                        for (DataSet data : bucket.getDataSets()) {
                            dumpDataSet(data, FILTER_TYPE_MONTH);
                        }
                    }

                    Utils.numberCountUp(findViewById(R.id.month_steps_text), String.valueOf(month_steps), 1000);

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "accessGoogleFit: Failure : " + e);
                });

        readHistoryData();
    }

    /**
     * 요청할 걸음수 Data Request형식 만들기
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private DataReadRequest setRequestDataStep(Long startTime, Long endTime) {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .enableServerQueries()
                .build();

        return readRequest;
    }

    /**
     * 요청할 거리 Data Request형식 만들기
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private DataReadRequest setRequestDataDistance(Long startTime, Long endTime) {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .enableServerQueries()
                .build();

        return readRequest;
    }

    private void readHistoryData() {
        // Invoke the History API to fetch the data with the query
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(dataSet -> {
                    dumpDataSet(dataSet);
                })
                .addOnFailureListener(e -> Log.d(TAG, "distance onFailure: e : " + e));
    }

    private void dumpDataSet(DataSet dataSet, int filterType) {
        DateFormat dateFormat = DateFormat.getTimeInstance();

        Log.d(TAG, "dumpDataSet: filterType : " + filterType);
        if (filterType == FILTER_TYPE_MONTH) {
        }
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart 시간: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd 시간: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {

                if (filterType == FILTER_TYPE_DAY) {
                    Log.d(TAG, "dumpDataSet: 일단위");
                    day_steps += Integer.parseInt(dp.getValue(field).toString());
                    Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field) + ", steps : " + day_steps);
                } else if (filterType == FILTER_TYPE_MONTH) {
                    Log.d(TAG, "dumpDataSet: 월 단위 요청");
                    month_steps += Integer.parseInt(dp.getValue(field).toString());
                    Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field) + ", steps : " + month_steps);

                }

            }

        }

    }

    private void dumpDataSet(DataSet dataSet) {
        DateFormat dateFormat = DateFormat.getTimeInstance();


        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart 시간: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd 시간: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                day_distance += Integer.parseInt(dp.getValue(field).toString());
                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field) + ", distance : " + day_distance);

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {

                accessGoogleFit();

            }

        }
    }


}