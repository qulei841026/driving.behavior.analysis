package com.carsmart.driving;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class VelocityLineChart extends View {

    private final String[] yText = new String[]{
            "Max",
            "40.0",
            "38.0", "36.0", "34.0", "32.0", "30.0", "28.0", "26.0", "24.0", "22.0", "20.0",
            "18.0", "16.0", "14.0", "12.0", "10.0", "8.0", "6.0", "4.0", "2.0", "0.0",
            "Min"};

    private final int minWidth = 400;
    private final int minHeight = 300;

    int w = minWidth;
    int h = minHeight;

    Paint mPaint = null;
    Rect tempRect = new Rect();

    int textColor;
    int lineColor;
    int pointColor;

    private ArrayList<Item> yItems = new ArrayList<>(yText.length);
    private ArrayList<Float> values;

    int ih;
    int baseX;
    int baseY;
    float baseValue;

    int px;
    public int maxCount;

    float space = 2.0f;

    public VelocityLineChart(Context context) {
        this(context, null);
    }

    public VelocityLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VelocityLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                getResources().getDisplayMetrics());
        textColor = getColor(R.color.text_color);
        lineColor = getColor(R.color.line_color);
        pointColor = getColor(R.color.point_color);

        initPaint();
        setBackgroundColor(getColor(R.color.background));


        for (String aYText : yText) {
            yItems.add(new Item(aYText));
        }

    }

    public void setValues(@Size(min = 1) List<Float> list) {
        values = new ArrayList<>(list);
        invalidate();
    }

    @SuppressWarnings("deprecation")
    private int getColor(int rid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(rid, getContext().getTheme());
        } else {
            return getResources().getColor(rid);
        }
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 抗锯齿
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_small));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int width, height;

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
        } else if (heightMeasureSpec == MeasureSpec.AT_MOST) {
            height = Math.min(heightSpecSize, minHeight);
        } else {
            height = minHeight;
        }

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            width = widthSpecSize;
        } else if (widthMeasureSpec == MeasureSpec.AT_MOST) {
            width = Math.min(widthSpecSize, minWidth);
        } else {
            width = minWidth;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        w = getWidth();
        h = getHeight();


        //文字宽高
        int th = getTextHeight(yText[0], mPaint);
        int tw = getTextWidth(yText[0], mPaint);

        //每一项高
        ih = h / yText.length;

        int dValue = (ih - th) / 2;
        int cih = ih / 2;

        for (int i = 0, size = yText.length; i < size; i++) {
            int h = i * ih;
            yItems.get(i).setY(dValue + h).setCY(cih + h);
        }

        maxCount = (w - tw) / px;       //X轴可容纳的数量

        Item item = yItems.get(yText.length - 2);

        baseX = tw + 3;
        baseY = item.cy;
        baseValue = Float.valueOf(item.text);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Item i : yItems) {
            mPaint.setColor(textColor);
            drawText(canvas, i.text, 0, i.y, mPaint);
            mPaint.setColor(lineColor);
            canvas.drawLine(baseX, i.cy, w, i.cy, mPaint);
        }

        if (values != null && values.size() > 0) {
            mPaint.setColor(pointColor);

            int x = baseX;
            int y = getValueY(values.get(0));
            for (int i = 0, size = values.size(); i < size - 1; i++) {
                int x2 = baseX + i * px;
                int y2 = getValueY(values.get(i + 1));

                canvas.drawLine(x, y, x2, y2, mPaint);    //波线
//                canvas.drawLine(x, y, x, baseY, mPaint);
                x = x2;
                y = y2;
            }
        }

    }

    private int getValueY(float f) {
        float value = f - baseValue;
        int i = (int) (value / space);
        int y = i * ih;
        float d = value - i * space;
        return baseY - (y + (int) (d * ih / space));
    }

    private void drawText(Canvas canvas, @NonNull String text, int x, int y, Paint paint) {
        paint.getTextBounds(text, 0, text.length() - 1, tempRect);
        canvas.drawText(text, x, Math.abs(tempRect.top) + y, mPaint);
    }

    protected int getTextHeight(@NonNull String text, @NonNull Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length() - 1, rect);
        return rect.bottom - rect.top;
    }

    protected int getTextWidth(@NonNull String text, @NonNull Paint paint) {
        return (int) paint.measureText(text);
    }

    class Item {

        int y;      //文件Y坐标
        int cy;     //基线Y坐标
        String text;

        Item(String text) {
            this.text = text;
        }

        Item setY(int y) {
            this.y = y;
            return this;
        }

        Item setCY(int cy) {
            this.cy = cy;
            return this;
        }
    }

}

