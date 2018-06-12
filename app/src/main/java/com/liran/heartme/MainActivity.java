package com.liran.heartme;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liran.heartme.models.BloodTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private EditText etTestName;
    private EditText etResult;
    private Button btnCheck;
    private TextView tvResult;
    private ArrayList<BloodTest> bloodTests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
        getData();
    }

    private void getData() {
        Ion.with(this)
                .load("https://s3.amazonaws.com/s3.helloheart.home.assignment/bloodTestConfig.json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            Type arrayList = new TypeToken<ArrayList<BloodTest>>() {
                            }.getType();
                            bloodTests = new Gson().fromJson(result.getAsJsonArray("bloodTestConfig"), arrayList);
                            Log.e("TAG", bloodTests.toString());
                        }

                    }
                });

    }

    private void findViews() {
        etTestName = findViewById(R.id.etTestName);
        etResult = findViewById(R.id.etResult);
        btnCheck = findViewById(R.id.btnCheck);
        tvResult = findViewById(R.id.tvResult);
    }

    private void setListeners() {
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if(!etTestName.getText().toString().matches("") && !etResult.getText().toString().matches("")) {
                    tvResult.setText(checkTestBlood());
                }else{
                    Toast.makeText(MainActivity.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * The method check if there is blood test with the name from the EditText, and if its true,
     * chech if the value good , not good , or the name is not recognize.
     */
    private String checkTestBlood() {
        String name = etTestName.getText().toString().toLowerCase();
        name = name.replaceAll("[^a-zA-Z0-9 ]+", "");

        int value = Integer.parseInt(etResult.getText().toString().trim());

        for (int i = 0; i < bloodTests.size(); i++) {
            BloodTest bloodTest = bloodTests.get(i);
            if (checkIfEquals(name, bloodTest.getName().toLowerCase())) {
                //same name , and need to check if the int is good
                if (value < bloodTest.getThreshold()) {
                    return "Good";
                } else {
                    return "Not good";
                }
            }
        }
        return "Not recognize";
    }

    private boolean checkIfEquals(String name, String bloodTestName) {
        String[] wordParts = name.split(" ");
        String[] bloodTestNameParts = bloodTestName.split(" ");
        if (wordParts.length == 1) {
            return name.equals(bloodTestName);
        } else {//2 words or more..
            Arrays.sort(wordParts);
            Arrays.sort(bloodTestNameParts);
            return Arrays.equals(wordParts, bloodTestNameParts);
        }
    }
}
