package com.jackiehou.myexample.hollow;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.jackiehou.myexample.R;


/**
 * Created by JackieHou.
 */

public class HollowTextView extends View {
    public static final String TAG = "HollowTextView";

    public static final int RECT = 0;

    public static final int OVAL = 1;

    public static final int ROUNDE = 2;

    private String text;


    private int textColor;

    private Typeface mTypeface;

    private int bgType = OVAL;

    PorterDuffXfermode mPorterDuffXfermode;

    private Paint mPaint;

    private Rect mBound;


    public HollowTextView(Context context) {
        this(context, null);
    }

    public HollowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        int textSize = 30;
        int textColor = Color.BLACK;
        int typeIndex = Typeface.BOLD;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HollowTextView);
            text = a.getString(R.styleable.HollowTextView_text);
            textSize = a.getDimensionPixelSize(R.styleable.HollowTextView_textSize, 30);
            textColor = a.getColor(R.styleable.HollowTextView_textColor, Color.BLACK);
            typeIndex = a.getInt(R.styleable.HollowTextView_textStyle, Typeface.BOLD);
            bgType = a.getInt(R.styleable.HollowTextView_shape, OVAL);
            a.recycle();
        }
        setTextSize(textSize);
        setTextColor(textColor);
        setTypeface(null,typeIndex);
        if (text == null) {
            text = "";
        }
        mBound = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            if(bgType == OVAL){
                //获取文本的矩形的高度和宽度
                mPaint.getTextBounds(text, 0, text.length(), mBound);
                width = (int) Math.sqrt(mBound.width()*mBound.width()+mBound.height()*mBound.height());
            }else {
                mPaint.getTextBounds(text, 0, text.length(), mBound);
                float textWidth = mBound.width();
                int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
                width = desired;
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            if(bgType == OVAL){
                height = width;
                mPaint.getTextBounds(text, 0, text.length(), mBound);
            }else {
                mPaint.getTextBounds(text, 0, text.length(), mBound);
                float textHeight = mBound.height();
                int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
                height = desired;
            }

        }
        setMeasuredDimension(width, height);
    }

    public final void setText(String text) {
        if(text == null){
            text = "";
        }
        this.text = text;
        requestLayout();
        invalidate();
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled
     * pixel" units.  This size is adjusted based on the current density and
     * user font size preference.
     *
     * @param size The scaled pixel size.
     *
     * @attr ref android.R.styleable#HollowTextView_textSize
     */
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * Set the default text size to a given unit and value.  See {@link
     * TypedValue} for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     *
     * @attr ref android.R.styleable#HollowTextView_textSize
     */
    public void setTextSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

        setRawTextSize(TypedValue.applyDimension(
                unit, size, r.getDisplayMetrics()));
    }

    private void setRawTextSize(float size) {
        if (size != mPaint.getTextSize()) {
            mPaint.setTextSize(size);
            if(mBound != null){
                requestLayout();
                invalidate();
            }
        }
    }

    public void setTextColor(int textColor) {
        if (textColor != mPaint.getColor()) {
            mPaint.setColor(textColor);
            if(mBound != null){
                invalidate();
            }
        }
    }

    public float getTextSize() {
        return mPaint.getTextSize();
    }

    public String getText() {
        return text;
    }

    public int getTextColor() {
        return mPaint.getColor();
    }

    /**
     * 设置文本的字体
     *
     * @param tf 文本图片上面文本的字体
     *
     * @attr ref android.R.styleable#HollowTextView_textStyle
     */
    public void setTypeface(Typeface tf) {
        if (mPaint.getTypeface() != tf) {
            mPaint.setTypeface(tf);
            if(mBound != null) {
                requestLayout();
                invalidate();
            }
        }
    }

    /**
     * 设置文本的字体
     *
     * @param tf    文本文本的字体
     *
     * @param style 文本的字体样式{@link Typeface#NORMAL} or {@link Typeface#BOLD} or
     *              {@link Typeface#ITALIC} or {@link Typeface#BOLD_ITALIC}
     *
     * @attr ref android.R.styleable#HollowTextView_textStyle
     */
    public void setTypeface(Typeface tf, int style) {
        if (style >= 0) {
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }
            setTypeface(tf);
        } else {
            setTypeface(tf);
        }
    }

    private void drawBg(Canvas canvas){
        if(bgType == OVAL){
            canvas.drawOval(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), mPaint);
        }else {
            canvas.drawColor(textColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //将绘制操作保存到新的图层（更官方的说法应该是离屏缓存）
        int sc = canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null, Canvas.ALL_SAVE_FLAG);

        //绘制desc源图
        drawBg(canvas);

        //设置混合模式   （只在源图像和目标图像相交的地方绘制目标图像）
        mPaint.setXfermode(mPorterDuffXfermode);
        //再绘制src源图
        canvas.drawText(text, 0, getHeight() / 2 + mBound.height() / 2, mPaint);

        //还原混合模式
        mPaint.setXfermode(null);

        //还原画布
        canvas.restoreToCount(sc);

    }
}
