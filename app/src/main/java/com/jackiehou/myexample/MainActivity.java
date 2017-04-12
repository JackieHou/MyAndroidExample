package com.jackiehou.myexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.HaloveHollowTextView;

import com.jackiehou.myexample.gaussianblur.GaussianBlurActivity;
import com.jackiehou.myexample.hollow.HollowActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                startActivity(new Intent(this, GaussianBlurActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(this, HollowActivity.class));
                break;
            default:
                break;
        }
    }
}
