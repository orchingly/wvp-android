package com.ly.wvp.device.onescreen

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ly.wvp.R
import com.ly.wvp.data.storage.SettingsConfig
import com.ly.wvp.device.play.MultiPlayManager
import com.ly.wvp.device.play.MultiVideoPlayer
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * 同屏播放列表
 * 原理:每个Player有独立的PlayerManager
 * @see [MultiVideoPlayer]
 */
class PlayListAdapter(private val mContext: Context,
                      private val mPlayList: ArrayList<SelectionItem>,
                      private val mConfig: SettingsConfig,
                      private val mPlayerHelper: ScrollPlayerHelper): RecyclerView.Adapter<PlayListAdapter.MultiPlayerHolder>() {

    companion object{
        private const val TAG = "PlayListAdapter"
    }

    private val mUrlLoader = ChannelUrlLoader(mConfig)

    fun bindSelection(selection: ArrayList<SelectionItem>){
        mPlayList.clear()
        mPlayList.addAll(selection)
    }

    /**
     * 返回一个拷贝
     */
    fun getPlayList(): List<SelectionItem>{
        return ArrayList(mPlayList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiPlayerHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        val holder = MultiPlayerHolder(
            LayoutInflater.from(mContext).inflate(R.layout.multi_player_holder_item, parent, false),
            mPlayerHelper
        )
        holder.loader = mUrlLoader
        holder.config = mConfig
        return holder
    }

    override fun getItemCount(): Int {
        return mPlayList.size
    }

    override fun onBindViewHolder(holder: MultiPlayerHolder, position: Int) {
        val item = mPlayList[position]
        holder.onBind(item, position)
    }

    override fun onViewRecycled(holder: MultiPlayerHolder) {
        Log.d(TAG, "onViewRecycled: release player ${holder.player}")
        holder.cancelPlayRequest()
        holder.releasePlayer()
    }

    class MultiPlayerHolder(itemView: View,
                            private val playerHelper: ScrollPlayerHelper
    ): RecyclerView.ViewHolder(itemView){

        companion object{
            private const val TAG = "PlayerHolder"
        }

        val player: MultiVideoPlayer = itemView.findViewById(R.id.multi_player)

        init {
            //隐藏标题
            player.titleTextView.visibility = View.GONE
            //隐藏返回键
            player.backButton.visibility = View.GONE
            //设置全屏按键功能
            player.fullscreenButton.setOnClickListener {
                resolveFullBtn(player)
            }
        }

        private fun resolveFullBtn(standardGSYVideoPlayer: StandardGSYVideoPlayer) {
            standardGSYVideoPlayer.startWindowFullscreen(itemView.context, true, true)
        }

        var loader: ChannelUrlLoader? = null

        var config: SettingsConfig? = null

        var playJob: Job? = null

        private val videoBuilder = GSYVideoOptionBuilder()

        fun onBind(item: SelectionItem, position: Int){
            val device = item.deviceId
            val channel = item.channelId
            //先设置唯一key, 然后再创建Builder的时候再用这里设置的值刷新一次,避免数据丢失
            player.playTag = TAG
            player.playPosition = position
            //Log.d(TAG, "onBind: $device, $channel, player:$player")
            //发起点播,获取url但是不播放
            requestUrl(device, channel, playerHelper)

//            player.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
//                //播放器可见
//                override fun onViewAttachedToWindow(v: View) {
//                }
//
//                //播放器完全不可见
//                override fun onViewDetachedFromWindow(v: View) {
//                    pauseVideo()
//                    player.removeOnAttachStateChangeListener(this)
//                }
//            })
        }

        /**
         * 先发起点播,请求到url后再自动播放视频
         */
        private fun requestUrl(device: String, channel: String, playerHelper: ScrollPlayerHelper) {
            playJob = CoroutineScope(IO).launch {
                loader?.let {
                    try {
                        Log.d(TAG, "requestUrl for :  $device, $channel, player:$player")
                        val stream = it.requestPlay(player.getKey(), device, channel)
                        yield()
                        launch(Main){
                            val url = if (config?.enableTls == true){
                                stream.getRtmps()
                            }
                            else{
                                stream.getRtmp()
                            }
                            Log.d(TAG, "requestUrl done.: url $url")
                            url?.apply {
                                buildMultiPlayer(this)
                                //第一次bind holder 没有滑动,可以直接播放, 如果是滚动列表时bind holder不能播放
                                playerHelper.tryPlay(this@MultiPlayerHolder)
                            }
                        }
                    }
                    catch (e: Exception){
                        Log.w(TAG, "requestUrlAutoPlay error: ${e.message},  $device, $channel")
                    }
                }
            }
        }

        private fun buildMultiPlayer(url: String){
            videoBuilder
                .setUrl(url)
                //保持当前player的tag和position, 否则被重置为默认值
                .setPlayTag(player.playTag)
                .setPlayPosition(player.playPosition)
                .setCacheWithPlay(true)
                .setIsTouchWiget(true)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setShowFullAnimation(false) //关闭全屏动画
                .setNeedLockFull(false)
                .setSeekRatio(1f)
                .setStartAfterPrepared(true)
                .setCacheWithPlay(false)

                .setVideoAllCallBack(object : GSYSampleCallBack() {

                    override fun onPrepared(url: String?, vararg objects: Any?) {
                        super.onPrepared(url, *objects)
                        if (!player.isIfCurrentIsFullscreen) {
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
                    }
                })
                .build(player)
        }

        fun cancelPlayRequest(){
            playJob?.cancel()
            loader?.cancelRequest(player.getKey())

        }

        fun releasePlayer() {
            val key = player.getKey()
            MultiPlayManager.releaseAllVideos(key)
            MultiPlayManager.removeManager(key)
        }

        fun pauseVideo() {
            MultiPlayManager.onPause( player.getKey())
        }

        /**
         * 没有播放过则startPlay, 如果播放过则resume
         */
        fun resumeVideo(){
            //播放过走onResume
            if (player.hadPlay()){
                Log.d(TAG, "resumeVideo: onResume")
                MultiPlayManager.onResume(player.getKey())
            }
            //第一次加载播放
            else{
                Log.d(TAG, "resumeVideo: startPlayLogic")
                player.startPlayLogic()
            }
        }
    }
}