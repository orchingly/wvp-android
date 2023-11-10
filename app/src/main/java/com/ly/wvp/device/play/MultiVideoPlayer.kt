package com.ly.wvp.device.play

import android.content.Context
import android.graphics.Point
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.text.TextUtils
import android.util.AttributeSet
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge

/**
 * 继承LiveVideoPlayer, 播放时自动隐藏进度条等菜单
 */
class MultiVideoPlayer: LiveVideoPlayer {

    companion object{
        const val TAG = "MultiVideoPlayer"
    }

    constructor (context: Context?, fullFlag: Boolean?): super(context, fullFlag) {
    }

    constructor(context: Context?): super(context) {
    }

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs) {
    }

    override fun init(context: Context?) {
        super.init(context)
        onAudioFocusChangeListener = OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {}
                AudioManager.AUDIOFOCUS_LOSS -> {}
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {}
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {}
            }
        }
        isStartAfterPrepared
    }

    override fun getGSYVideoManager(): GSYVideoViewBridge {
        MultiPlayManager.getManager(getKey()).initContext(context.applicationContext)
        return MultiPlayManager.getManager(getKey())
    }

    override fun backFromFull(context: Context): Boolean {
        return MultiPlayManager.backFromWindowFull(context, getKey())
    }

    override fun releaseVideos() {
        MultiPlayManager.releaseAllVideos(getKey())
    }


    override fun getFullId(): Int {
        return MultiPlayManager.FULLSCREEN_ID
    }

    override fun getSmallId(): Int {
        return MultiPlayManager.SMALL_ID
    }

    override fun startWindowFullscreen(
        context: Context,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        return gsyBaseVideoPlayer as MultiVideoPlayer
    }


    override fun showSmallVideo(
        size: Point?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        //下面这里替换成你自己的强制转化
        val multiSampleVideo = super.showSmallVideo(
                size,
                actionBar,
                statusBar
            ) as MultiVideoPlayer
        multiSampleVideo.mStartButton.visibility = GONE
        multiSampleVideo.mStartButton = null
        return multiSampleVideo
    }

    fun getKey(): String {
        if (mPlayPosition == -22) {
            Debuger.printfError(javaClass.simpleName + " used getKey() " + "******* PlayPosition never set. ********")
        }
        if (TextUtils.isEmpty(mPlayTag)) {
            Debuger.printfError(javaClass.simpleName + " used getKey() " + "******* PlayTag never set. ********")
        }
        return TAG + mPlayPosition + mPlayTag
    }

    override fun toString(): String {
        return getKey()
    }

    /**
     * 是否播放过
     */
    fun hadPlay(): Boolean{
        return mHadPlay
    }

}