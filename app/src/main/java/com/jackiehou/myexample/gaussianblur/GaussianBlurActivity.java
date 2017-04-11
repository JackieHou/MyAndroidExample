package com.jackiehou.myexample.gaussianblur;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jackiehou.myexample.R;

/**
 * Created by jackiehou.
 */

public class GaussianBlurActivity extends Activity {

    public static final String TAG = "GaussianBlurActivity";

    //可拖动的背景为高斯模糊图片的一个plane
    FrameLayout mBlurPlane;
    //mBlurPlane的高斯模糊图片背景控件
    ImageView mBlurIv;

    private float mYPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaussianblur);
        mBlurPlane = (FrameLayout) findViewById(R.id.gaussian_blur_plane);
        mBlurIv = (ImageView)findViewById(R.id.gaussian_blur_iv);
        //初始化设置mBlurPlane的偏移量和模糊背景view的偏移量
        initBlurPlaneOffset();
        //把壁纸图片设置为content的背景,把模糊化的壁纸图片设置为mBlurIv的背景
        setBlurBitmap();

        //一个简单的根据手指上下拖动(没有做touch up之后的动画)
        mBlurPlane.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mYPos = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float delta = event.getY() - mYPos;
                        moveBlurPlane(delta);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 初始化设置mBlurPlane的偏移量和mBlurPlane的模糊背景view的偏移量
     */
    private void initBlurPlaneOffset() {
        DisplayMetrics  dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //窗口高度
        int screenHeight = dm.heightPixels;
        //偏移量为屏幕高度减去两个statusbar的高度和一个actionbar的高度
        float initOffset = screenHeight - getStatusBarHeight()*2-getActionBarHeight();
        Log.i(TAG, "initBlurPlaneOffset initOffset=" + initOffset);
        //mBlurPlane向上偏移initOffset
        mBlurPlane.setTranslationY(-initOffset);
        //mBlurIv向下偏移initOffset
        mBlurIv.setTranslationY(initOffset);
    }

    /**
     * @return 状态栏高度
     */
    private int getStatusBarHeight() {
        int statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * @return ActionBar高度
     */
    public int getActionBarHeight() {
        int actionBarHeight = 0;
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getActionBar() != null && getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        Log.i(TAG, "getActionBarHeight actionBarHeight=" + actionBarHeight);
        return actionBarHeight;
    }

    private void setBlurBitmap() {
        //把content背景设置为系统壁纸
        Drawable defaultWallpaper = WallpaperManager.getInstance(this).getDrawable();
        findViewById(R.id.content_fl).setBackground(defaultWallpaper);
        //得到高斯模糊化的壁纸图片
        Bitmap background = ((BitmapDrawable) defaultWallpaper).getBitmap();
        Bitmap blurBitmap = blurBitmap(background,this);

        BitmapDrawable gaosiBitmapDrawable = new BitmapDrawable(getResources(), blurBitmap);
        mBlurIv.setBackground(gaosiBitmapDrawable);
    }

    /**
     * mBlurPlane up , mBlurIv down;
     * mBlurPlane down , mBlurIv up;
     * @param delta
     */
    private void moveBlurPlane(float delta) {
        Log.i(TAG,"moveBlurPlane fl.getTranslationY() ="+mBlurPlane.getTranslationY()+", delta ="+delta);

        mBlurPlane.setTranslationY(mBlurPlane.getTranslationY()+delta);
        mBlurIv.setTranslationY(mBlurIv.getTranslationY()-delta);
    }

    /**
     * 高斯模糊
     * @param bitmap
     * @param context
     * @return
     */
    public  static Bitmap blurBitmap(Bitmap bitmap ,Context context){


        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context.getApplicationContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        //bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }

}
