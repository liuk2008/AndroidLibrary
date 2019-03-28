package com.android.common.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.android.common.R;


/**
 * 自定义输入框
 * Created by Administrator on 2016/11/1.
 */
public class PasswordView extends android.support.v7.widget.AppCompatEditText {
    private static final String TAG = "PasswordView";
    private Paint bordPaint;//外框画笔
    private Paint linePaint;//线的画笔
    private Paint passTextPaint;//密码画笔
    private int width;
    private int height;
    private int passwordLength = 8;// 代码的长度
    private float passwordTextSize = 48;//字体大小
    private int textLength;
    private String content;

    public PasswordView(Context context) {
        super(context);
        initPaint();
    }

    public PasswordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PasswordView);
        passwordLength = array.getInt(R.styleable.PasswordView_password_length, 8);
        passwordTextSize = array.getFloat(R.styleable.PasswordView_password_textSize, 48);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(passwordLength)});
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setCursorVisible(false);

        bordPaint = new Paint();
        bordPaint.setColor(Color.parseColor("#cccccc"));
        bordPaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#cccccc"));

        passTextPaint = new Paint();
        passTextPaint.setColor(Color.parseColor("#000000"));
        passTextPaint.setTextSize(passwordTextSize);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredWidth() / passwordLength;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawRoundRect(canvas);
        drawLine(canvas);
        drawTextPass(canvas);
    }

    /**
     * 绘制密码
     *
     * @param canvas
     */
    private void drawTextPass(Canvas canvas) {
        float cx = (width / passwordLength) / (float) 2.0;
        float cy = height / (float) 2.0;
        if (TextUtils.isEmpty(content)) return;
        for (int i = 0; i < textLength; i++) {
            canvas.drawCircle(width * i / passwordLength + cx, cy, 15, passTextPaint);
        }
    }

    /**
     * 绘制线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        for (int i = 1; i < passwordLength; i++) {
            float x = width * i / passwordLength;
            canvas.drawLine(x, 0, x, height, linePaint);
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawRoundRect(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = width - 1;
        rectF.bottom = height - 1;
        canvas.drawRect(rectF, bordPaint);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        textLength = text.toString().length();
        content = text.toString();
    }

}