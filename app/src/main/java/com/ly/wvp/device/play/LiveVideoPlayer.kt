package com.ly.wvp.device.play

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.ly.wvp.record.play.SampleCoverVideo

open class LiveVideoPlayer: SampleCoverVideo {

    companion object{
        const val TAG = "LiveVideoPlayer"
    }

    constructor (context: Context?, fullFlag: Boolean?): super(context, fullFlag) {
    }

    constructor(context: Context?): super(context) {
    }

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs) {
    }

    override fun touchDoubleUp(e: MotionEvent?) {
        //禁用双击
    }

    override fun init(context: Context?) {
        super.init(context)
    }

    /**
     * 禁用左右快进
     */
    override fun touchSurfaceMove(deltaX: Float, deltaY: Float, y: Float) {
        if (mChangePosition){
            return
        }
        super.touchSurfaceMove(deltaX, deltaY, y)
    }

    /***************** 下方重载方法，在播放开始不显示底部进度和按键 ********************/
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        //直播禁用底部进度条和弹窗进度条
        hideLiveCommonWidget()
    }

    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
        hideLiveCommonWidget()
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        hideLiveCommonWidget()
    }

    override fun changeUiToNormal() {
        super.changeUiToNormal()
        hideLiveCommonWidget()
    }

    override fun startAfterPrepared() {
        super.startAfterPrepared()
        hideLiveCommonWidget()
    }

    override fun changeUiToCompleteClear() {
        super.changeUiToCompleteClear()
        hideLiveCommonWidget()
    }

    override fun hideAllWidget() {
        super.hideAllWidget()
        setViewShowState(mBottomProgressBar, INVISIBLE)
    }

    /**
     * 隐藏直播不需要的公共控件
     */
    private fun hideLiveCommonWidget(){
        setViewShowState(mProgressBar, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mTotalTimeTextView, INVISIBLE)
    }

    override fun changeUiToPlayingClear() {
        super.changeUiToPlayingClear()
        setViewShowState(mBottomProgressBar, INVISIBLE)
    }

    //DEBUG visibility changed
//    override fun setViewShowState(view: View?, visibility: Int) {
//        if (view == mBottomProgressBar && visibility == VISIBLE){
//            Log.w(TAG, "setViewShowState: ", Throwable("DEBUG") )
//        }
//        super.setViewShowState(view, visibility)
//    }
}