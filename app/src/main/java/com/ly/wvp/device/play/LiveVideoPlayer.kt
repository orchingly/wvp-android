package com.ly.wvp.device.play

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.ly.wvp.R
import com.ly.wvp.util.toString
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer

class LiveVideoPlayer: StandardGSYVideoPlayer {

    private lateinit var mMoreScale: TextView

    private lateinit var mChangeRotate: TextView

    private lateinit var mChangeTransform: TextView

    private lateinit var loadFailed: TextView

    //记住切换数据源类型
    private var mType = 0

    private var mTransformSize = 0

    //数据源
    private var mSourcePosition = 0

    private var context: Context

    constructor(context: Context): super(context){
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        this.context = context
    }

    override fun init(context: Context?) {
        super.init(context)
        initView()
    }

    private fun initView() {
        mMoreScale = findViewById<View>(R.id.moreScale) as TextView
        mChangeRotate = findViewById<View>(R.id.change_rotate) as TextView
        mChangeTransform = findViewById<View>(R.id.change_transform) as TextView
        loadFailed = findViewById(R.id.video_load_failed)

        //隐藏进度条
        findViewById<ProgressBar>(R.id.progress).visibility = INVISIBLE

        //切换比例
        mMoreScale.setOnClickListener(OnClickListener {
            if (!mHadPlay) {
                return@OnClickListener
            }
            when (mType) {
                0 -> {
                    mType = 1
                }
                1 -> {
                    mType = 2
                }
                2 -> {
                    mType = 3
                }
                3 -> {
                    mType = 4
                }
                4 -> {
                    mType = 0
                }
            }
            resolveTypeUI()
        })

        //旋转播放角度
        mChangeRotate.setOnClickListener(OnClickListener {
            if (!mHadPlay) {
                return@OnClickListener
            }
            if (mTextureView.rotation - mRotate == 270f) {
                mTextureView.rotation = mRotate.toFloat()
                mTextureView.requestLayout()
            } else {
                mTextureView.rotation = mTextureView.rotation + 90
                mTextureView.requestLayout()
            }
        })

        //镜像旋转
        mChangeTransform.setOnClickListener(OnClickListener {
            if (!mHadPlay) {
                return@OnClickListener
            }
            when (mTransformSize) {
                0 -> {
                    mTransformSize = 1
                }
                1 -> {
                    mTransformSize = 2
                }
                2 -> {
                    mTransformSize = 0
                }
            }
            resolveTransform()
        })
    }

    override fun onSurfaceSizeChanged(surface: Surface?, width: Int, height: Int) {
        super.onSurfaceSizeChanged(surface, width, height)
        resolveTransform()
    }

    /**
     * 处理显示逻辑
     */
    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
        resolveRotateUI()
        resolveTransform()
    }

    /**
     * 处理镜像旋转
     * 注意，暂停时
     */
    private fun resolveTransform() {
        when (mTransformSize) {
            1 -> {
                val transform = Matrix()
                transform.setScale(-1f, 1f, (mTextureView.width / 2).toFloat(), 0f)
                mTextureView.setTransform(transform)
                mChangeTransform.text = "左右镜像"
                mTextureView.invalidate()
            }

            2 -> {
                val transform = Matrix()
                transform.setScale(1f, -1f, 0f, (mTextureView.height / 2).toFloat())
                mTextureView.setTransform(transform)
                mChangeTransform.text = "上下镜像"
                mTextureView.invalidate()
            }

            0 -> {
                val transform = Matrix()
                transform.setScale(1f, 1f, (mTextureView.width / 2).toFloat(), 0f)
                mTextureView.setTransform(transform)
                mChangeTransform.text = "旋转镜像"
                mTextureView.invalidate()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.relative_layout_live_player
    }


    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    override fun startWindowFullscreen(
        context: Context?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val player = super.startWindowFullscreen(
                context,
                actionBar,
                statusBar
        ) as LiveVideoPlayer
//        player.mSourcePosition = mSourcePosition
//        player.mType = mType
//        player.mTransformSize = mTransformSize
//        player.resolveTypeUI()
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        return player
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer?
    ) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        if (gsyVideoPlayer != null) {
            val detailVideo: LiveVideoPlayer =
                gsyVideoPlayer as LiveVideoPlayer
            mSourcePosition = detailVideo.mSourcePosition
            mType = detailVideo.mType
            mTransformSize = detailVideo.mTransformSize
            resolveTypeUI()
        }
    }

    /**
     * 旋转逻辑
     */
    private fun resolveRotateUI() {
        if (!mHadPlay) {
            return
        }
        mTextureView.rotation = mRotate.toFloat()
        mTextureView.requestLayout()
    }

    /**
     * 显示比例
     * 注意，GSYVideoType.setShowType是全局静态生效，除非重启APP。
     */
    private fun resolveTypeUI() {
        if (!mHadPlay) {
            return
        }
        when (mType) {
            1 -> {
                mMoreScale.text = R.string.display_size_16_9.toString(context)
                GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9)
            }
            2 -> {
                mMoreScale.text = R.string.display_size_4_3.toString(context)
                GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_4_3)
            }
            3 -> {
                mMoreScale.text = R.string.full_screen.toString(context)
                GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
            }
            4 -> {
                mMoreScale.text =R.string.full_screen_pull.toString(context)
                GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL)
            }
            0 -> {
                mMoreScale.text = R.string.screen_default_size.toString(context)
                GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT)
            }
        }
        changeTextureViewShowType()
        if (mTextureView != null) mTextureView.requestLayout()
    }

    fun showLoadFailed(){
        loadFailed.visibility = VISIBLE
    }

    fun hideFailed(){
        loadFailed.visibility = GONE
    }

    fun startPlay(){
        super.clickStartIcon()
    }

    /**
     * 直播不需要双击
     */
    override fun touchDoubleUp(e: MotionEvent?) {
        return
    }

//    /**
//     * 直播不需要触摸
//     */
//    override fun touchSurfaceMove(deltaX: Float, deltaY: Float, y: Float) {
//        return
//    }

//    /**
//     * 直播不需要触摸
//     */
//    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
//        return
//    }
//
//    /**
//     * 直播不需要触摸
//     */
//    override fun touchSurfaceUp() {
//        return
//    }
}