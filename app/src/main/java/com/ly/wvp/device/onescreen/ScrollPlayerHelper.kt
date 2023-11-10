package com.ly.wvp.device.onescreen

import android.graphics.Rect
import android.util.ArrayMap
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.ly.wvp.device.play.MultiVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView.CURRENT_STATE_PAUSE
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView.CURRENT_STATE_PLAYING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 计算滑动，管理自动播放
 *
 */

class ScrollPlayerHelper {
    companion object{
        private const val TAG = "ScrollPlayerHelper"
    }

    private var firstVisible = 0
    private var lastVisible = 0
    private var visibleCount = 0

    private val mJobMap = ArrayMap<String, Job>()

    /**
     * 缓存正在播放的Holder
     * 控制暂停
     */
    private val mPlayingHolderMap = ArrayMap<String, PlayListAdapter.MultiPlayerHolder>()

    /**
     * 可见区域的播放器Holder
     */
    private val mPlayerVisibleMap = ArrayMap<String, PlayListAdapter.MultiPlayerHolder>()

    private var isScrolling = false
    fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
        when (scrollState) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                isScrolling = false
                Log.d(TAG, "onScrollStateChanged: scroll stop")
                playVideo(view)
            }
            else -> {
                Log.d(TAG, "onScrollStateChanged: scrolling")
                isScrolling = true
            }
        }
    }

    fun onScroll(
        recyclerView: RecyclerView,
        firstVisibleItem: Int,
        lastVisibleItem: Int,
        visibleItemCount: Int
    ) {
        if (firstVisible == firstVisibleItem) {
            return
        }
        firstVisible = firstVisibleItem
        lastVisible = lastVisibleItem
        visibleCount = visibleItemCount
        findVisibleHolderPlayer(recyclerView)
        //暂停完全不可见的player
        pauseVideoFullInvisible()
    }

    private fun findVisibleHolderPlayer(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager?:return
        mPlayerVisibleMap.clear()
        for (i in 0 .. visibleCount) {
            val child = layoutManager.getChildAt(i)?: continue
            val holder = recyclerView.getChildViewHolder(child) as PlayListAdapter.MultiPlayerHolder?
            holder?.let {
                if (checkPlayerVisible(it.player)) {
                    Log.d(TAG, "findVisibleHolderPlayer: visible key = ${it.player.getKey()}")
                    mPlayerVisibleMap[it.player.getKey()] = it
                }
            }
        }
    }

    /**
     * pause playing invisible
     */
    private fun pauseVideoFullInvisible() {
//        mPlayingHolderMap.forEach { (k,v)->
//            Log.d(TAG, "pauseVideoFullInvisible: playing key = $k")
//            //正在播放的player变为不可见, pause video
//            if (!mPlayerVisibleMap.contains(k)){
//                Log.d(TAG, "pauseVideoFullInvisible: pause invisible key = $k")
//                v.pauseVideo()
//            }
//        }
        val iterator = mPlayingHolderMap.iterator()
        while (iterator.hasNext()){
            val entry = iterator.next()
            val key = entry.key
            val holder = entry.value
            if (!mPlayerVisibleMap.contains(key)){
                Log.d(TAG, "pauseVideoFullInvisible: pause invisible key = $key")
                //Video paused should be removed from cache
                holder.pauseVideo()
                iterator.remove()
            }
        }
    }

    private fun playVideo(view: RecyclerView) {
        val layoutManager = view.layoutManager?: kotlin.run {
            Log.d(TAG, "playVideo: layoutManager is null")
            return
        }
        if (visibleCount <= 0 ){
            return
        }
        for (i in 0 .. visibleCount) {
            val child = layoutManager.getChildAt(i)?: continue
            val holder = view.getChildViewHolder(child) as PlayListAdapter.MultiPlayerHolder
            val player = holder.player

            //一半可视,筛选符合播放条件的holder
            //条件:完全可见,CURRENT_STATE_NORMAL, CURRENT_STATE_ERROR, CURRENT_STATE_PAUSE, CURRENT_STATE_PLAYING 正在播放也算进去,后面再用正在播放的缓存筛选
            //未初始化状态-1,不能播放, 因为bindViewHolder未返回url
            if (checkHalfVisible(player)) {
                Log.d(TAG, "playVideo: $i, ${player.currentPlayer.currentState}")
                if (player.currentPlayer.currentState == GSYBaseVideoPlayer.CURRENT_STATE_NORMAL
                    || player.currentPlayer.currentState == GSYBaseVideoPlayer.CURRENT_STATE_ERROR
                    || player.currentPlayer.currentState == CURRENT_STATE_PAUSE
                    || player.currentPlayer.currentState == CURRENT_STATE_PLAYING) {
                    Log.d(TAG, "playVideo: $i, ${player.getKey()}")
                    startPlay(holder)
                }
            }
        }
    }

    /**
     * 底部item超过一半可见
     */
/*    private fun checkBottomVisible(player: MultiVideoPlayer): Boolean {
        val h = player.measuredHeight
        val rect = Rect()
        val visible = player.getLocalVisibleRect(rect)
        //超过一半可见
        return visible && rect.top == 0 && rect.bottom >= h / 2
    }*/

    /**
     * 顶部item超过一半可见
     */
/*
    private fun checkTopVisible(player: MultiVideoPlayer): Boolean {
        val h = player.measuredHeight
        val rect = Rect()
        val visible = player.getLocalVisibleRect(rect)
        //超过一半可见
        return visible && rect.top <= h / 2 && rect.bottom == h
    }
*/


    /**
     * 通用超过一半可见
     */
    private fun checkHalfVisible(player: MultiVideoPlayer): Boolean {
        val h = player.measuredHeight
        val rect = Rect()
        val visible = player.getLocalVisibleRect(rect)
        //超过一半可见
        return visible && rect.top <= h / 2 && rect.bottom >= h / 2
    }

    /**
     * 完全可见
     */
/*
    private fun checkPlayerFullVisible(player: MultiVideoPlayer): Boolean{
        val rect = Rect()
        val visible = player.getLocalVisibleRect(rect)
        val height = player.measuredHeight
        //完全可视
        return visible && rect.top == 0 && rect.bottom == height
    }
*/

    /**
     * @return 检查是否可见,只要有一点可见就返回true
     */
    private fun checkPlayerVisible(player: MultiVideoPlayer): Boolean{
        val rect = Rect()
        return player.getLocalVisibleRect(rect)
    }

    /**
     * 两条播放逻辑
     * 1.初次创建列表
     * 2.滑动停止
     */
    private fun startPlay(holder: PlayListAdapter.MultiPlayerHolder) {
        val key = holder.player.getKey()
        //任务未结束,继续播放
        if (mJobMap.contains(key)){
            Log.d(TAG, "startPlay: cancel running job $key")
            mJobMap[key]?.cancel()
        }

        val job = CoroutineScope(Main).launch {
            try {
                delay(400)
                Log.d(TAG, "startPlay: job for $key")
                mPlayingHolderMap[key] = holder
                holder.resumeVideo()
                //开始播放逻辑结束,移除任务
                mJobMap.remove(key)
            }
            catch (ignored: Exception){
            }
        }
        //缓存任务
        mJobMap[key] = job
    }

    fun tryPlay(holder: PlayListAdapter.MultiPlayerHolder){
        //滑动中,不允许播放
        if (isScrolling){
            Log.d(TAG, "tryPlay: scrolling not allow play")
            return
        }
        if (checkHalfVisible(holder.player)){
            Log.d(TAG, "tryPlay: not scroll, call start play")
            startPlay(holder)
        }else{
            Log.d(TAG, "tryPlay: not half visible, not allow play")
        }

    }
}