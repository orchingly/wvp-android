package com.ly.wvp.widget.progress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ly.wvp.R;

import java.util.ArrayList;
import java.util.List;

public class SubsectionSeekBar extends View {
    private Context mContext;

    // 默认总进度
    private int mMax = 1000;

    // 当前进度 相对max值而言
    private int mProgress = 0;

    // 第二条进度条值
    private int mSecondaryProgress = 0;

    // bar 默认背景色
    private int backgroundColor;

    // bar 默认进度条颜色
    private int progressColor;

    // 第二进度条颜色
    private int secondaryProgressColor;

    // 按钮图片ID
    private int seekBarResIdNormal;
    private int seekBarResIdPressed;

    // 按钮颜色
    private int seekBarColorNormal;
    private int seekBarColorPressed;

    //key点颜色
    private int keyBarColorNormal;
    private int keyBarColorPressed;
    /**
     * 判断是否被点击
     */
    private boolean isTouch = false;

    private boolean isKey = false;

    // 分段背景色值
    private List<SectionBean> sectionBeans = new ArrayList<>();

    //key点
    private List<Integer> keybars = new ArrayList<>();

    /**
     * 进度条的高度
     */
    private float seekBarHeight;

    /**
     * view的半径
     */
    private int seekBarRadius;

    private float keyBarRadius;


    // 当前进度百分比
    private float percent;

    // 背景色画笔
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // key点画笔
    private Paint mKeyBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // SeekBar按钮的位置
    private float lineTop, lineBottom, lineLeft, lineRight;

    // 圆角
    private int lineCorners;

    // Bar 的宽度
    private float lineWidth;

    // 总体背景色
    private RectF line = new RectF();

    // 已走完进度背景色
    private RectF progressLine = new RectF();

    // 第二进度颜色
    private RectF secondaryProgressLine = new RectF();

    // 导航按钮
    private SeekBar seekBar = new SeekBar();

    // SubsectionSeekBar 监听
    private OnSubsectionSeekBarChangeListener onSubsectionSeekBarChangeListener;

    //===============参数方法分割线=============================================================

    public SubsectionSeekBar(Context context) {
        super(context);
    }

    public SubsectionSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.SubsectionSeekBar);
        mMax = t.getInt(R.styleable.SubsectionSeekBar_max, 1000);
        seekBarResIdNormal = t.getResourceId(R.styleable.SubsectionSeekBar_seekBarResIdNormal, 0);
        seekBarResIdPressed = t.getResourceId(R.styleable.SubsectionSeekBar_seekBarResIdPressed, 0);
        backgroundColor = t.getColor(R.styleable.SubsectionSeekBar_backgroundColor, Color.parseColor("#D7D7D7"));
        progressColor = t.getColor(R.styleable.SubsectionSeekBar_progressColor, Color.parseColor("#ff30c47f"));
        secondaryProgressColor = t.getColor(R.styleable.SubsectionSeekBar_secondaryProgressColor, Color.parseColor("#ccffffff"));
        seekBarHeight = t.getDimension(R.styleable.SubsectionSeekBar_seekBarHeight, 4);
        keyBarRadius = t.getDimension(R.styleable.SubsectionSeekBar_keyBarRadius, 12);
        seekBarColorNormal = t.getColor(R.styleable.SubsectionSeekBar_seekBarColorNormal, Color.parseColor("#FF7F50"));
        seekBarColorPressed = t.getColor(R.styleable.SubsectionSeekBar_seekBarColorPressed, Color.parseColor("#FF4500"));
        keyBarColorNormal = t.getColor(R.styleable.SubsectionSeekBar_KeyBarColorNormal, Color.parseColor("#999999"));
        keyBarColorPressed = t.getColor(R.styleable.SubsectionSeekBar_KeyBarColorPressed, Color.parseColor("#09D198"));
//        t.close();
        t.recycle();
    }

    /**
     * 设置最大值
     *
     * @param max 最大值
     */
    public void setMax(int max) {
        this.mMax = max;
    }

    /**
     * 设置进度
     *
     * @param progress 进度值
     */
    public void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException(" progress 不可小于 0");
        } else if (progress > mMax) {
            progress = mMax;
        }
        this.mProgress = progress;
        updateSeekBar(progress);
    }

    public int getProgress() {
        return mProgress;
    }

    /**
     * 设置第二进度
     *
     * @param secondaryProgress 第二进度
     */
    public void setSecondaryProgress(int secondaryProgress) {
        this.mSecondaryProgress = secondaryProgress;
        invalidate();
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setSecondaryProgressColor(int secondaryProgressColor) {
        this.secondaryProgressColor = secondaryProgressColor;
    }

    public void setSectionBeans(List<SectionBean> sectionBeans) {
        this.sectionBeans.clear();
        this.sectionBeans.addAll(sectionBeans);
        this.invalidate();
    }

    public void setKayBars(List<Integer> keybars) {
        this.keybars.clear();
        this.keybars.addAll(keybars);
        this.invalidate();
    }

    /**
     * 设置监听
     *
     * @param onSubsectionSeekBarChangeListener 监听
     */
    public void setOnSubsectionSeekBarChangeListener(OnSubsectionSeekBarChangeListener onSubsectionSeekBarChangeListener) {
        this.onSubsectionSeekBarChangeListener = onSubsectionSeekBarChangeListener;
    }

    //================测量绘制Touch时间分割线==============================================
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSize > widthSize) {
            setMeasuredDimension(widthSize, widthSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * @param w    当前view的宽
     * @param h    当前view的高
     * @param oldw 改变之后的宽
     * @param oldh 改变之后的高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        seekBarRadius = h / 2;
//        seekBarRadius = 0;
        /*
         * 属性 left right top bottom 描述了SeekBar按钮的位置
         * 然后根据它们预先设置确定出 RectF line 背景的三维
         * lineCorners 圆滑的边缘似乎会比直角更好看
         */
//        lineLeft = seekBarRadius;
        //两端padding对齐
        lineLeft = getPaddingStart();
//        lineRight = w - seekBarRadius;
        lineRight = w - getPaddingEnd();
//        lineTop = seekBarRadius - seekBarRadius / seekBarHeight;
//        lineBottom = seekBarRadius + seekBarRadius / seekBarHeight;
        if (seekBarHeight > h || seekBarHeight == 0) {
            seekBarHeight = seekBarRadius;
        } else {
            seekBarHeight = seekBarHeight / 2;
        }

        if (keyBarRadius > h || keyBarRadius == 0) {
            keyBarRadius = seekBarRadius / 2;
        }

        lineTop = seekBarRadius - seekBarHeight;
        lineBottom = seekBarRadius + seekBarHeight;

        //事件进度条底部和拖动进度条顶部对齐
        if (getParent() instanceof ConstraintLayout) {
            ViewGroup vp = (ConstraintLayout) getParent();
            android.widget.SeekBar seekBar = vp.findViewById(R.id.progress);
            lineBottom =  h -  (float) (seekBar.getMeasuredHeight() + seekBar.getMaxHeight())/ 2;
        }


        //progress宽度
        lineWidth = lineRight - lineLeft;
        //左上右下
        line.set(lineLeft, lineTop, lineRight, lineBottom);
        //圆角
        lineCorners = /*(int) ((lineBottom - lineTop) * 0.45f)*/0;
        // 在RangeSeekBar确定尺寸时确定SeekBar按钮尺寸
        seekBar.onSizeChanged(seekBarRadius, seekBarRadius, h, seekBarRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);

        mKeyBarPaint.setStyle(Paint.Style.FILL);
        mKeyBarPaint.setAntiAlias(true);

//        drawBackground(canvas);
        Log.d("TAG", "drawSubsectionBean onDraw: " + this);
        if (sectionBeans != null && sectionBeans.size() > 0) {
            for (int i = 0; i < sectionBeans.size(); i++) {
                drawSubsectionBean(canvas, sectionBeans.get(i));
            }
        }
//        drawSecondaryProgress(canvas);
//        drawProgress(canvas);

//        if (keybars != null && keybars.size() > 0) {
//            for (int i = 0; i < keybars.size(); i++) {
//                drawKeyBar(canvas, keybars.get(i));
//            }
//        }

        // 绘制按钮
//        seekBar.draw(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     */
    private void drawBackground(Canvas canvas) {
        mBackgroundPaint.setColor(backgroundColor);
        /*
         * rect：RectF对象。
         * rx：x方向上的圆角半径。
         * ry：y方向上的圆角半径。
         * paint：绘制时所使用的画笔。
         */
        canvas.drawRoundRect(line, lineCorners, lineCorners, mBackgroundPaint);
    }

    /**
     * 绘制分段背景
     *
     * @param canvas      画布
     * @param sectionBean 分段背景bean类
     */
    private void drawSubsectionBean(Canvas canvas, SectionBean sectionBean) {
        //起点
        int origin = sectionBean.getOrigin();
        //终点
        int terminus = sectionBean.getTerminus();
        if (0 <= origin && origin < terminus & terminus <= mMax) {
            //起点位置 绘制
            int originLeft = (int) (origin * 1f / mMax * lineWidth);
            //终点位置 绘制
            int terminusRight = (int) (terminus * 1f / mMax * lineWidth);
            mBackgroundPaint.setColor(sectionBean.getColor());
            RectF subsectionLine = new RectF();
            //绘制到顶部top=0
            subsectionLine.set(lineLeft + originLeft, 0, lineLeft + terminusRight, lineBottom);
            //渐变
//            LinearGradient linearGradient = new LinearGradient(0,lineBottom,0,0,0xFF0033,0x1FFF0033, Shader.TileMode.CLAMP);

//            mBackgroundPaint.setShader(linearGradient);
            Log.d("TAG", "drawSubsectionBean: " + subsectionLine +", " + lineCorners +", lineBottom = " + lineBottom);
            canvas.drawRoundRect(subsectionLine, lineCorners, lineCorners, mBackgroundPaint);
        } else {
            //直接手动抛出异常
            throw new RuntimeException("坐标位置错误，\n" +
                    "1：起点小于0；\n" +
                    "2：起点大于终点；\n" +
                    "3：终点小于总长");
        }
    }

    /**
     * 绘制key点
     *
     * @param canvas 画布
     * @param point  key点位置
     */
    private void drawKeyBar(Canvas canvas, int point) {
        if (point > mProgress) {
            mKeyBarPaint.setColor(keyBarColorNormal);
        } else {
            mKeyBarPaint.setColor(keyBarColorPressed);
        }
        int offset = (int) (lineWidth * point / mMax); //左边距离
        canvas.drawCircle(lineLeft + offset, seekBarRadius, keyBarRadius, mKeyBarPaint);
    }

    /**
     * 绘制第二条进度条的颜色
     *
     * @param canvas 画布
     */
    private void drawSecondaryProgress(Canvas canvas) {
        mBackgroundPaint.setColor(secondaryProgressColor);
        int secondaryprogressLineWidth = (int) (mSecondaryProgress * 1f / mMax * lineWidth);
        secondaryProgressLine.set(lineLeft, lineTop, secondaryprogressLineWidth + lineLeft, lineBottom);
        canvas.drawRoundRect(secondaryProgressLine, lineCorners, lineCorners, mBackgroundPaint);
    }

    /**
     * 绘制已走过进度的颜色
     *
     * @param canvas 画布
     */
    private void drawProgress(Canvas canvas) {
        int progressLineWidth = (int) (lineWidth * percent);
        progressLine.set(lineLeft, lineTop, progressLineWidth + lineLeft, lineBottom);
        mBackgroundPaint.setColor(progressColor);
        canvas.drawRoundRect(progressLine, lineCorners, lineCorners, mBackgroundPaint);
    }

    /**
     * 点击监听
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击位置坐标 x
        float x = event.getX();
        // bar的位置
        if (x <= lineLeft) {
            percent = 0;
            mProgress = 0;
        } else if (x >= lineRight) {
            percent = 1;
            mProgress = mMax;
        } else {
            percent = (x - lineLeft) * 1f / (lineWidth);
            mProgress = (int) ((x - lineLeft) * 1f / (lineWidth) * mMax);
        }

        int checkProgress = checkProgress(this.mProgress);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (checkKeyBar(x)) {
                    isKey = true;
                    return true;
                }
                isKey = false;
                isTouch = true;
                if (onSubsectionSeekBarChangeListener != null) {
                    onSubsectionSeekBarChangeListener.onStartTrackingTouch(this);
                }
            case MotionEvent.ACTION_MOVE:
                if (isKey && checkKeyBar(x)) {
                    return true;
                }
                isKey = false;
                this.mProgress = checkProgress;
                updateSeekBar(this.mProgress);
                if (onSubsectionSeekBarChangeListener != null) {
                    onSubsectionSeekBarChangeListener.onProgressChanged(this, this.mProgress, true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (onSubsectionSeekBarChangeListener != null && checkProgress != mProgress) {
                    onSubsectionSeekBarChangeListener.onStopTrackingTouch(this);
                }
                if (checkKeyBar(x)) {
                    return true;
                }
                isTouch = false;
                updateSeekBar(this.mProgress);
                if (onSubsectionSeekBarChangeListener != null && checkProgress != mProgress) {
                    onSubsectionSeekBarChangeListener.onProgressChanged(this, this.mProgress, true);
                }
                break;
        }
        return true;
    }

    /**
     * 根据进度调整bar位置
     *
     * @param percent 0-1之间
     */
    public void updateSeekBar(float percent) {
        // SeekBar按钮根据当前手指在拖动条上的滑动而滑动
        if (percent < 0) {
            throw new IllegalArgumentException(" percent 不可小于 0");
        } else if (percent > 1) {
            percent = 1;
        }
        seekBar.slide(percent);
        this.invalidate();
    }

    /**
     * 根据进度调转bar位置
     *
     * @param progress 0-max 之间
     */
    public void updateSeekBar(int progress) {
        // SeekBar按钮根据当前手指在拖动条上的滑动而滑动
        percent = progress * 1f / mMax;
        updateSeekBar(percent);
    }

    /**
     * 判断该点是否处在被禁止范围内
     *
     * @param progress 要判断的点坐标
     * @return 返回处理后的点坐标，
     * 如果处于被限制坐标内，返回离其最近的位置。
     * 如果不是处于被限制坐标内，返回处理后的点坐标
     */
    public int checkProgress(int progress) {
        for (int i = 0; i < sectionBeans.size(); i++) {
            SectionBean sectionBean = sectionBeans.get(i);
            if (sectionBean.isSkip()) {
                int origin = sectionBean.getOrigin();
                int terminus = sectionBean.getTerminus();
                if (origin < progress && progress < terminus) {
                    return terminus + 1;
                }
            }
        }
        return progress;
    }

    public boolean checkKeyBar(float x) {
        for (int i = 0; i < keybars.size(); i++) {
            int offset = (int) (lineLeft + lineWidth * keybars.get(i) / mMax); //key点中心距离
            if (x > offset - keyBarRadius && x < offset + keyBarRadius) {
                if (onSubsectionSeekBarChangeListener != null) {
                    onSubsectionSeekBarChangeListener.onKeyTouch(i, offset);
                }
                return true;
            }
        }
        return false;
    }

    public class SeekBar {

        /**
         * 按钮位置，百分比
         */
        float currPercent;

        /**
         * 移动后的左边界
         */
        int left;

        /**
         * 导航点中心坐标
         */
        int centerX;
        int centerY;

        /**
         * 导航圈半径
         */
        int radius;
        /**
         * 导航圆圈的bar
         */
        private Paint paintNormal;
        private Paint paintPressed;

        /**
         * 如果按钮是张图片
         */
        private Bitmap bmpNormal;
        private Bitmap bmpPressed;

        /**
         * 当RangeSeekBar尺寸发生变化时，SeekBar按钮尺寸随之变化
         *
         * @param centerX SeekBar按钮的X中心在RangeSeekBar中的相对位置
         * @param centerY SeekBar按钮的Y中心在RangeSeekBar中的相对位置
         */
        void onSizeChanged(int centerX, int centerY, int hSize, int radius) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
            if (seekBarResIdNormal > 0 && seekBarResIdPressed > 0) {
                bmpNormal = creatBmp(hSize, seekBarResIdNormal);
                bmpPressed = creatBmp(hSize, seekBarResIdPressed);
            } else {
                initPaint();
            }
        }

        /**
         * 把ID转换成bmp
         *
         * @param size 尺寸
         * @param id   图片ID
         * @return 转换成的Bitmap
         */
        private Bitmap creatBmp(int size, int id) {
            Bitmap originalBmp = BitmapFactory.decodeResource(mContext.getResources(), id);
            if (originalBmp != null) {
                Matrix matrix = new Matrix();
                float scaleHeight = ((float) size) / originalBmp.getHeight();
                //等比按高度缩放
                matrix.postScale(scaleHeight, scaleHeight);
                return Bitmap.createBitmap(originalBmp, 0, 0, originalBmp.getWidth(), originalBmp.getHeight(), matrix, true);
            }
            return null;
        }

        //初始化画笔
        private void initPaint() {
            paintNormal = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintNormal.setColor(seekBarColorNormal);
            paintPressed = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintPressed.setColor(seekBarColorPressed);
        }

        void draw(Canvas canvas) {
            int offset = (int) (lineWidth * currPercent);
            canvas.save();
            canvas.translate(offset, 0);
            //如果被点击
            if (isTouch) {
                if (bmpPressed != null) {
                    canvas.drawBitmap(bmpPressed, left, left, null);
                } else {
                    canvas.translate(left, 0);
                    canvas.drawCircle(centerX, centerY, radius, paintPressed);
                }
            } else {
                if (bmpNormal != null) {
                    canvas.drawBitmap(bmpNormal, left, left, null);
                } else {
                    canvas.translate(left, 0);
                    canvas.drawCircle(centerX, centerY, radius, paintNormal);
                }
            }
            canvas.restore();
        }

//        boolean collide(MotionEvent event) {
//            //判断是否被点击
//            float x = event.getX();
//            float y = event.getY();
//            int offset = (int) (lineWidth * currPercent);
//            return x > left + offset && x < right + offset && y > top && y < bottom;
//        }

        /**
         * 滑动
         *
         * @param percent 滑动百分比
         */
        void slide(float percent) {
            if (percent < 0) {
                percent = 0;
            } else if (percent > 1) {
                percent = 1;
            }
            currPercent = percent;
        }
    }
}