package com.android.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.android.common.utils.system.DensityUtils;


/**
 * Created by liuk on 2019/5/5
 */
public class TitleView extends androidx.appcompat.widget.AppCompatTextView {

    private Paint linePaint;
    private Paint textPaint;

    public TitleView(Context context) {
        super(context);
        initPaint(context);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }

    private void initPaint(Context context) {
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#ffd773"));
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(DensityUtils.dip2px(context, 5));
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#353535"));
//        Typeface font = Typeface.create(Typeface.SERIF, Typeface.BOLD);
//        textPaint.setTypeface(font);
        textPaint.setFakeBoldText(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        textPaint.setTextSize(getTextSize());
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        RectF rectF = new RectF(0, (getHeight() / 2) * 1.2f, getWidth() / 2, getHeight() * 0.8f);
        canvas.drawRoundRect(rectF, getHeight() / 4, getHeight() / 4, linePaint);
        canvas.drawText(getText().toString(), 0, getHeight() / 2 + dy, textPaint);
    }

}
