package com.ly.wvp.widget.check

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.ly.wvp.R

class FinishTicket : View {
    private var mCustomSize = 0 //画布大小
    private var mRadius = 0
    private var mCheckBaseColor = 0 //选中状态基本颜色
    private var mCheckTickColor = 0 //选中状态对号颜色
    private var mUnCheckTickColor = 0 //未选中状态对号颜色
    private var mUnCheckBaseColor = 0 //未选中状态基本颜色
    private var mCheckPaint: Paint? = null //选中状态画笔 下面的背景圆
    private var mCheckArcPaint: Paint? = null //选中状态画笔 下面的背景圆圆弧
    private var mCheckDeclinePaint: Paint? = null //选中状态画笔  （上面的随动画缩减的圆盖在上面）  和对号
    private var mUnCheckPaint: Paint? = null //未选中状态画笔
    private var mCheckTickPaint: Paint? = null //选中对号画笔
    private var mCheckPaintArc: Paint? = null //回弹圆画笔 设置不同宽度已达到回弹圆动画目的
    private var isCheckd = false //选中状态
    private lateinit var mPoints: FloatArray
    private var mCenter = 0
    private var mRectF: RectF? = null
    private var mRingCounter = 0
    private var mCircleCounter = 0 //盖在上面的背景色圆逐渐缩小  逆向思维模拟向圆心收缩动画
    private var mAlphaCount = 0
    private var scaleCounter = 50
    private var mRectArc: RectF? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(context, attrs) //获取自定义属性
        initPaint() //初始化画笔
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        0
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mCustomSize, mCustomSize)
    }

    override fun onDraw(canvas: Canvas) {
        if (mCustomSize > 0) {
            if (!isCheckd) {
                canvas.drawCircle(
                    mCenter.toFloat(),
                    mCenter.toFloat(),
                    mRadius.toFloat(),
                    mUnCheckPaint!!
                ) //未选中状态的圆
                canvas.drawLines(mPoints, mUnCheckPaint!!)
                return
            }
            mRingCounter += 10
            if (mRingCounter >= 360) {
                mRingCounter = 360
            }
            canvas.drawArc(mRectF!!, 90f, mRingCounter.toFloat(), false, mCheckArcPaint!!)
            if (mRingCounter == 360) {
                //先绘制指定颜色的圆
                canvas.drawCircle(
                    mCenter.toFloat(),
                    mCenter.toFloat(),
                    mRadius.toFloat(),
                    mCheckPaint!!
                )
                //然后在指定颜色的图层上，再绘制背景色的圆(半径不断缩小) 半径不断缩小，背景就不断露出来，达到向中心收缩的效果
                mCircleCounter += 10
                canvas.drawCircle(
                    mCenter.toFloat(),
                    mCenter.toFloat(),
                    (mRadius - mCircleCounter).toFloat(),
                    mCheckDeclinePaint!!
                )
                if (mCircleCounter >= mRadius + 100) {
                    mAlphaCount += 20
                    if (mAlphaCount >= 255) mAlphaCount = 255 //显示对号（外加一个透明的渐变）
                    mCheckTickPaint!!.alpha = mAlphaCount //设置透明度
                    //画白色的对号
                    canvas.drawLines(mPoints, mCheckTickPaint!!)
                    scaleCounter -= 4 //获取是否回弹
                    if (scaleCounter <= -50) { //scaleCounter从大于0到小于0的过程中 画笔宽度也是由增加到减少最后减为0 实现了圆放大收缩的回弹效果
                        scaleCounter = -50
                    }
                    //放大并回弹，设置画笔的宽度
                    val strokeWith = mCheckArcPaint!!.strokeWidth +
                            if (scaleCounter > 0) 6 else -6
                    println(strokeWith)
                    mCheckArcPaint!!.strokeWidth = strokeWith
                    canvas.drawArc(mRectArc!!, 90f, 360f, false, mCheckArcPaint!!)
                }
            }
            postInvalidate() //重绘
        }
    }

    /**
     * 获取自定义属性
     *
     * @param context
     * @param attrs
     */
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FinishTicket)
        mCustomSize =
            typedArray.getDimension(R.styleable.FinishTicket_custom_size, dip2px(130f).toFloat())
                .toInt()
        mCheckBaseColor =
            typedArray.getColor(R.styleable.FinishTicket_check_base_color, mCheckBaseColor)
        mCheckTickColor =
            typedArray.getColor(R.styleable.FinishTicket_check_tick_color, mCheckTickColor)
        mUnCheckBaseColor =
            typedArray.getColor(R.styleable.FinishTicket_uncheck_base_color, mUnCheckBaseColor)
        mUnCheckTickColor =
            typedArray.getColor(R.styleable.FinishTicket_uncheck_tick_color, mUnCheckTickColor)
        typedArray.recycle()
        mCenter = mCustomSize / 2
        mRadius = mCenter - 50 //缩小圆半径大小 防止回弹动画弹出画布
        mPoints = FloatArray(8)
        //简易模拟对号 未做适配
        mPoints[0] = (mCenter - mCenter / 3).toFloat()
        mPoints[1] = mCenter.toFloat()
        mPoints[2] = mCenter.toFloat()
        mPoints[3] = (mCenter + mCenter / 4).toFloat()
        mPoints[4] = (mCenter - 8).toFloat()
        mPoints[5] = (mCenter + mCenter / 4).toFloat()
        mPoints[6] = (mCenter + mCenter / 2).toFloat()
        mPoints[7] = (mCenter - mCenter / 5).toFloat()
        mRectF = RectF(
            (mCenter - mRadius).toFloat(),
            (mCenter - mRadius).toFloat(),
            (mCenter + mRadius).toFloat(),
            (mCenter + mRadius).toFloat()
        ) //选中状态的圆弧 动画
        mRectArc = RectF(
            (mCenter - mRadius).toFloat(),
            (mCenter - mRadius).toFloat(),
            (mCenter + mRadius).toFloat(),
            (mCenter + mRadius).toFloat()
        ) //选中状态的圆弧 动画
    }

    /***
     * 初始化画笔
     */
    private fun initPaint() {
        mCheckPaint = Paint()
        mCheckPaint!!.isAntiAlias = true
        mCheckPaint!!.color = mCheckBaseColor
        mCheckPaintArc = Paint()
        mCheckPaintArc!!.isAntiAlias = true
        mCheckPaintArc!!.color = mCheckBaseColor
        mCheckArcPaint = Paint()
        mCheckArcPaint!!.isAntiAlias = true
        mCheckArcPaint!!.color = mCheckBaseColor
        mCheckArcPaint!!.style = Paint.Style.STROKE
        mCheckArcPaint!!.strokeWidth = 20f
        mCheckDeclinePaint = Paint()
        mCheckDeclinePaint!!.isAntiAlias = true
        mCheckDeclinePaint!!.color = Color.parseColor("#3E3E3E")
        mUnCheckPaint = Paint()
        mUnCheckPaint!!.isAntiAlias = true
        mUnCheckPaint!!.color = mUnCheckBaseColor
        mUnCheckPaint!!.style = Paint.Style.STROKE
        mUnCheckPaint!!.strokeWidth = 20f
        mCheckTickPaint = Paint()
        mCheckTickPaint!!.isAntiAlias = true
        mCheckTickPaint!!.color = mCheckTickColor
        mCheckTickPaint!!.style = Paint.Style.STROKE
        mCheckTickPaint!!.strokeWidth = 20f
    }

    /**
     * 重置
     */
    private fun reset() {
        mRingCounter = 0
        mCircleCounter = 0
        mAlphaCount = 0
        scaleCounter = 50
        mCheckArcPaint!!.strokeWidth = 20f //画笔宽度重置
        postInvalidate()
    }

    /**
     * dp转px
     *
     * @param dpValue
     * @return
     */
    fun dip2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 开始做选中动画
     */
    fun setUpEvent() {
        //未选中,先反选再重新做一个选中动画
        if (isCheckd){
            isCheckd = false
            reset()
        }
        isCheckd = !isCheckd
        reset()
    }


    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    interface OnCheckedChangeListener {
        fun onCheckedChanged(tickView: FinishTicket?, isCheckd: Boolean)
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        mOnCheckedChangeListener = listener
    }
}