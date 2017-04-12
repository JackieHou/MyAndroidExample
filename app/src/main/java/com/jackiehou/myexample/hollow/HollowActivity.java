package com.jackiehou.myexample.hollow;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import com.jackiehou.myexample.R;
import com.jackiehou.myexample.gaussianblur.GaussianBlurActivity;

/**
 * Created by jackiehou .
 */

public class HollowActivity extends Activity implements View.OnClickListener {

    EditText mEditText;
    HollowTextView mHollowTextView;
    private TypedArray sColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hollow);
        Drawable defaultWallpaper = WallpaperManager.getInstance(this).getDrawable();
        getWindow().setBackgroundDrawable(defaultWallpaper);

        mEditText = (EditText) findViewById(R.id.count_down_tv);
        mHollowTextView = (HollowTextView) findViewById(android.R.id.text1);

        sColors = getResources().obtainTypedArray(R.array.letter_tile_colors);

        findViewById(R.id.big_btn).setOnClickListener(this);
        findViewById(R.id.small_btn).setOnClickListener(this);
        findViewById(R.id.countdown_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.big_btn:
                changedSize(8);
                break;
            case R.id.small_btn:
                changedSize(-8);
                break;
            case R.id.countdown_btn:
                countDown();
                break;
            default:
                break;
        }
    }

    private void countDown() {
        int count = 10;
        CharSequence charSequence = mEditText.getText();
        if(!TextUtils.isEmpty(charSequence)){
            count = Integer.parseInt(charSequence.toString());
            if(count <=0){
                count = 10;
            }
        }
        final int c = count;
        CountDownTimer countDownTimer = new CountDownTimer(count*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long times = millisUntilFinished/1000;
                String timeStr = String.valueOf(times);
                mHollowTextView.setTextColor(pickColor(timeStr));
                mHollowTextView.setText(timeStr);
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.onTick(count*1000);
        countDownTimer.start();
    }

    private int pickColor(final String identifier) {
        // String.hashCode() implementation is not supposed to change across java versions, so
        // this should guarantee the same email address always maps to the same color.
        // The email should already have been normalized by the ContactRequest.
        final int color = Math.abs(identifier.hashCode()) % sColors.length();
        return sColors.getColor(color, Color.BLACK);
    }

    private void changedSize(int i) {
        float size = mHollowTextView.getTextSize();
        size = size+i;
        if(size<20){
            return;
        }
        mHollowTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
    }
}
