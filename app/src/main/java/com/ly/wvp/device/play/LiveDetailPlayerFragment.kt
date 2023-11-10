package com.ly.wvp.device.play

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ly.wvp.R
import com.ly.wvp.data.model.StreamContent
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.util.shortToast
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer


class LiveDetailPlayerFragment : Fragment() {

    companion object {
        private const val TAG = "LiveDetailPlayerFragment"
        private const val ENABLE_HTTPS = false
    }

    private lateinit var viewModel: LiveDetailPlayerViewModel

    private lateinit var detailPlayer: LiveVideoPlayer
//    private lateinit var orientationUtils: OrientationUtils

    private lateinit var deviceId: String
    private lateinit var channelId: String

    private lateinit var liveInfo: TextView
    private lateinit var storage: DataStorage

    private var isPlay = false

    private lateinit var mActionBack: ImageView

    private lateinit var mTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live_detail_player_frament, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailPlayer = view.findViewById(R.id.detail_player)
        liveInfo = view.findViewById(R.id.live_info)
        mActionBack = view.findViewById(R.id.action_bar_back_img)
        mTitle = view.findViewById(R.id.action_bar_content_title)
        storage = DataStorage.getInstance(requireContext())
//        resolveNormalVideoUI()
        initVideo()
        viewModel = ViewModelProvider(this)[LiveDetailPlayerViewModel::class.java]
        viewModel.setConfig(storage.getConfig())
        //视频流请求参数
        deviceId = LiveDetailPlayerFragmentArgs.fromBundle(requireArguments()).deviceId
        channelId = LiveDetailPlayerFragmentArgs.fromBundle(requireArguments()).channelId
        mTitle.text = channelId
        liveInfo.text = channelId
        viewModel.requestStream(deviceId, channelId)

        viewModel.getStream().observe(viewLifecycleOwner){
//            detailPlayer.hideFailed()
            playLiveVideo(it)
        }

        viewModel.getNetError().observe(viewLifecycleOwner){
            it.getReason().shortToast(requireContext())
//            detailPlayer.showLoadFailed()
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //退出全屏播放
                if (!exitFullWindow()){
                    //不是全屏播放,返回上一层fragment
                    Log.d(TAG, "handleOnBackPressed: popBackStack")
                    findNavController().popBackStack()
                }
            }
        })

        mActionBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun exitFullWindow(): Boolean {
        if (GSYVideoManager.backFromWindowFull(requireContext())) {
            Log.d(TAG, "handleTouchEvent: back true")
            return true
        }
        return false
    }

    private fun playLiveVideo(streamContent: StreamContent){
        //rtmp低延迟 < 1s,hls延迟10-30s
        val url = if (ENABLE_HTTPS){
            streamContent.getRtmps()
        }
        else{
            streamContent.getRtmp()
        }
        url?.let {
//            initVideoBuilderMode(url)
            buildGSYVideoOptionBuilder(url).build(detailPlayer)
            //自动播放
//            detailPlayer.startPlay()
            detailPlayer.startPlayLogic()
        }?: kotlin.run{
            Log.d(TAG, "playLiveVideo: url is null")
        }
    }

    private fun initVideo(){
        //外部辅助的旋转，帮助全屏
//        orientationUtils = OrientationUtils(activity, detailPlayer, null)
//        //初始化不打开外部的旋转
//        orientationUtils.isEnable = false
        //全屏切换
//        detailPlayer.fullscreenButton.setOnClickListener {
//            Log.d(TAG, "initVideo: startWindowFullscreen")
//            showFull()
//        }
//        detailPlayer.isShowFullAnimation = true
        initLiveVideoOptions()


//        buildGSYVideoOptionBuilder("").build(detailPlayer)
        //隐藏标题
        detailPlayer.titleTextView.visibility = View.GONE
        //隐藏返回键
        detailPlayer.backButton.visibility = View.GONE
        //设置全屏按键功能
        detailPlayer.fullscreenButton.setOnClickListener {
            resolveFullBtn(detailPlayer)
        }

    }

    private fun resolveFullBtn(standardGSYVideoPlayer: StandardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(context, true, true)
    }

    /**
     *优化直播参数
     */
    private fun initLiveVideoOptions(){
        //直播配置
        val list: MutableList<VideoOptionModel> = ArrayList()
        var videoOptionModel = VideoOptionModel(
            IjkMediaPlayer.OPT_CATEGORY_FORMAT,
            "allowed_media_types",
            "video"
        ) //根据媒体类型来配置

        list.add(videoOptionModel)
        videoOptionModel = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316)
        list.add(videoOptionModel)
        videoOptionModel = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1) // 无限读
        list.add(videoOptionModel)
        videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
        list.add(videoOptionModel)
        videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0)
        list.add(videoOptionModel)
        videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48)
        list.add(videoOptionModel)
        videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8)
        list.add(videoOptionModel)

        //设置启动时的探测时间
        list.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 100))
        list.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100))
        //设置探测区大小
        list.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10 * 1024))
        //关闭播放缓冲区
        list.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0))
        //立刻写出处理完的Packet
        list.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1))
        //允许丢帧
        list.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1))
        //优化进度跳转
        list.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1))
        list.add(VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek"))


        GSYVideoManager.instance().optionModelList = list

        //可以加快解析
        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT)
        //END
    }

    private fun buildGSYVideoOptionBuilder(url: String): GSYVideoOptionBuilder {
        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT)
        return GSYVideoOptionBuilder()
            .setUrl(url)
            .setCacheWithPlay(false)
            .setVideoTitle(channelId)
            .setIsTouchWiget(true)
            .setRotateViewAuto(true)
            .setLockLand(true)
            .setShowFullAnimation(false) //打开动画
            .setNeedLockFull(false)
            .setSeekRatio(1f)
            .setStartAfterPrepared(true)
            .setCacheWithPlay(false)
            .setPlayTag(TAG)

            .setVideoAllCallBack(object : GSYSampleCallBack() {

                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    if (!detailPlayer.isIfCurrentIsFullscreen) {
                        //静音
                        GSYVideoManager.instance().isNeedMute = true
                    }
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                    //全屏不静音
                    GSYVideoManager.instance().isNeedMute = true
                }

                override fun onEnterFullscreen(url: String?, vararg objects: Any?) {
                    super.onEnterFullscreen(url, *objects)
                    GSYVideoManager.instance().isNeedMute = false
                    //不显示标题
//                    player.currentPlayer.titleTextView.text = objects[0].toString()
                }
            })

    }


    override fun onPause() {
        super.onPause()
//        orientationUtils.setIsPause(true)
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
//        orientationUtils.setIsPause(false)
        //直播false
        GSYVideoManager.onResume(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isPlay) {
            detailPlayer.currentPlayer.release()
        }
//        orientationUtils.releaseListener()
        GSYVideoManager.releaseAllVideos()
    }

}