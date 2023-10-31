package com.ly.wvp.widget.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.ly.wvp.calendar.Calendar
import com.ly.wvp.calendar.MonthView


class CalenderMothView(context: Context) : MonthView(context) {
    /**
     * 自定义标记的文本画笔
     */
    private val mTextPaint = Paint()

    /**
     * 自定义标记的圆形背景
     */
    private val mSchemeBasicPaint = Paint()
    private val mRadio: Float
    private val mPadding: Int
    private val mSchemeBaseLine: Float

    init {
        mTextPaint.textSize = dipToPx(context, 8f).toFloat()
        mTextPaint.color = -0x1
        mTextPaint.isAntiAlias = true
        mTextPaint.isFakeBoldText = true
        mSchemeBasicPaint.isAntiAlias = true
        mSchemeBasicPaint.style = Paint.Style.FILL
        mSchemeBasicPaint.textAlign = Paint.Align.CENTER
        mSchemeBasicPaint.isFakeBoldText = true
        mRadio = dipToPx(getContext(), 7f).toFloat()
        mPadding = dipToPx(getContext(), 4f)
        val metrics = mSchemeBasicPaint.fontMetrics
        mSchemeBaseLine = mRadio - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(
            getContext(),
            1f
        )

//        //兼容硬件加速无效的代码
//        setLayerType(View.LAYER_TYPE_SOFTWARE, mSchemeBasicPaint);
//        //4.0以上硬件加速会导致无效
//        mSchemeBasicPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.SOLID));
    }

    /**
     * 绘制选中的日子
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    override fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        mSelectedPaint.style = Paint.Style.FILL
        canvas.drawRect(
            (x + mPadding).toFloat(),
            (y + mPadding).toFloat(),
            (x + mItemWidth - mPadding).toFloat(),
            (y + mItemHeight - mPadding).toFloat(),
            mSelectedPaint
        )
        return true
    }

    /**
     * 绘制标记的事件日子
     *
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     * @param y        日历Card y起点坐标
     */
    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        mSchemeBasicPaint.color = calendar.schemeColor
        canvas.drawCircle(
            x + mItemWidth - mPadding - mRadio / 2,
            y + mPadding + mRadio,
            mRadio,
            mSchemeBasicPaint
        )
        canvas.drawText(
            calendar.scheme,
            x + mItemWidth - mPadding - mRadio / 2 - getTextWidth(calendar.scheme) / 2,
            y + mPadding + mSchemeBaseLine, mTextPaint
        )
    }

    private fun getTextWidth(text: String): Float {
        return mTextPaint.measureText(text)
    }

    /**
     * 绘制文本
     *
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标
     * @param y          日历Card y起点坐标
     * @param hasScheme  是否是标记的日期
     * @param isSelected 是否选中
     */
    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val cx = x + mItemWidth / 2
        val top = y - mItemHeight / 6
        val isInRange = isInRange(calendar)
        if (isSelected) {
            canvas.drawText(
                calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                mSelectTextPaint
            )
            canvas.drawText(
                calendar.lunar,
                cx.toFloat(),
                mTextBaseLine + y + mItemHeight / 10,
                mSelectedLunarTextPaint
            )
        } else if (hasScheme) {
            canvas.drawText(
                calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                if (calendar.isCurrentMonth && isInRange) mSchemeTextPaint else mOtherMonthTextPaint
            )
            canvas.drawText(
                calendar.lunar,
                cx.toFloat(),
                mTextBaseLine + y + mItemHeight / 10,
                mCurMonthLunarTextPaint
            )
        } else {
            canvas.drawText(
                calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth && isInRange) mCurMonthTextPaint else mOtherMonthTextPaint
            )
            canvas.drawText(
                calendar.lunar, cx.toFloat(), mTextBaseLine + y + mItemHeight / 10,
                if (calendar.isCurrentDay && isInRange) mCurDayLunarTextPaint else if (calendar.isCurrentMonth) mCurMonthLunarTextPaint else mOtherMonthLunarTextPaint
            )
        }
    }

    companion object {
        /**
         * dp转px
         *
         * @param context context
         * @param dpValue dp
         * @return px
         */
        private fun dipToPx(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}