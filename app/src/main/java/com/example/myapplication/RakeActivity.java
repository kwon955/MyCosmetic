package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RakeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rake);
    }
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.id1:
                Intent intent2 = getIntent();
                intent2.putExtra("result", 1);
                setResult(1, intent2);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.id2:
                Intent intent = getIntent();
                intent.putExtra("result", 2);
                setResult(2, intent);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.id3:
                break;
        }
    }
}
