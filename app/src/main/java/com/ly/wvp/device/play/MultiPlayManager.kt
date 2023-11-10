package com.ly.wvp.device.play

import android.content.Context
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.ly.wvp.R
import com.shuyu.gsyvideoplayer.GSYVideoBaseManager
import com.shuyu.gsyvideoplayer.player.IPlayerManager
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.utils.CommonUtil

class MultiPlayManager: GSYVideoBaseManager() {

    init {
        init()
    }

    override fun getPlayManager(): IPlayerManager {
        return IjkPlayerManager()
    }

    companion object{
        
        val SMALL_ID: Int = R.id.custom_small_id

        val FULLSCREEN_ID: Int = R.id.custom_full_id

        const val TAG = "MultiPlayManager"

        private val sMap: ArrayMap<String, MultiPlayManager> = ArrayMap()

        /**
         * 单例管理器
         */
        @Synchronized
        fun getManager(key: String): MultiPlayManager {
            var manager: MultiPlayManager? = sMap[key]
            return if (manager == null){
                manager = MultiPlayManager()
                sMap[key] = manager
                Log.d(TAG, "getManager: new manager : $manager, key=$key")
                manager
            }
            else{
                //Log.d(TAG, "getManager: $manager, key=$key")
                manager
            }
        }


        /**
         * 退出全屏，主要用于返回键
         *
         * @return 返回是否全屏
         */
        fun backFromWindowFull(context: Context, key: String): Boolean {
            var backFrom = false
            val player = CommonUtil.scanForActivity(context).findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
            val oldF = player.findViewById<View>(FULLSCREEN_ID)
            if (oldF != null) {
                backFrom = true
                CommonUtil.hideNavKey(context)
                getManager(key).lastListener()?.onBackFullscreen()
            }
            return backFrom
        }

        /**
         * 页面销毁了记得调用是否所有的video
         */
        fun releaseAllVideos(key: String) {
            Log.d(TAG, "releaseAllVideos: video $key")
            if (getManager(key).listener() != null) {
                getManager(key).listener()?.onCompletion()
            }
            getManager(key).releaseMediaPlayer()
        }

        fun onPauseAll() {
            sMap.forEach { (k, _) ->
                onPause(k)
            }
        }

        fun onResumeAll() {
            sMap.forEach { (k, _) ->
                onResume(k)
            }
        }

        fun clearAllVideo() {
            sMap.forEach { (k, _) ->
                releaseAllVideos(k)
            }
            sMap.clear()
        }

        fun removeManager(key: String?) {
            sMap.remove(key)
        }

        /**
         * 当前是否全屏状态
         *
         * @return 当前是否全屏状态， true代表是。
         */
        /*fun isFullState(activity: Activity?): Boolean {
            val vp = CommonUtil.scanForActivity(activity).findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
            val full = vp.findViewById<View>(FULLSCREEN_ID)
            var gsyVideoPlayer: GSYVideoPlayer? = null
            if (full != null) {
                gsyVideoPlayer = full as GSYVideoPlayer
            }
            return gsyVideoPlayer != null
        }*/


        /**
         * 恢复播放
         */
        fun onResume(key: String) {
            if (getManager(key).listener() != null) {
                Log.d(TAG, "onResume: video $key")
                getManager(key).listener()?.onVideoResume()
            }
        }

        /**
         * 暂停播放
         */
        fun onPause(key: String) {
            if (getManager(key).listener() != null) {
                Log.d(TAG, "onPause: video $key")
                getManager(key).listener()?.onVideoPause()
            }
        }
    }


}