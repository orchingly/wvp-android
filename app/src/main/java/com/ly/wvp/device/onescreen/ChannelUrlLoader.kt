package com.ly.wvp.device.onescreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ly.wvp.auth.HttpConnectionClient
import com.ly.wvp.auth.NetError
import com.ly.wvp.auth.ResponseBody
import com.ly.wvp.auth.ServerUrl
import com.ly.wvp.data.model.StreamContent
import com.ly.wvp.data.storage.SettingsConfig
import com.ly.wvp.util.JsonParseUtil
import okhttp3.Call
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

/**
 *
 */
class ChannelUrlLoader(private val config: SettingsConfig) {

    companion object{
        const val TAG = "ChannelUrlLoader"
    }

    /**
     * 缓存http请求, 提供取消请求接口
     */
    private val mRequestCallCache = ConcurrentHashMap<String, Call>()

    private val _netError = MutableLiveData<NetError>()

    //fun getNetError(): LiveData<NetError> = _netError

    /**
     * 发起点播, 需要在协程中调用
     * 线程安全
     */
    fun requestPlay(key: String, deviceId: String, channelId: String): StreamContent {
        val httpUrl = HttpConnectionClient.buildPublicHeader(config)
            .addPathSegments(ServerUrl.API_PLAY)
            .addPathSegment(ServerUrl.START)
            .addPathSegment(deviceId)
            .addPathSegment(channelId)
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()

        Log.d(TAG, "requestPlay: url = ${httpUrl.toUrl()}")
        val call = HttpConnectionClient.newCall(request)
        mRequestCallCache[key] = call
        call.execute().run {
            this.body?.let {
                try {
                    val jsonObj = JSONObject(it.string())
                    val code = jsonObj.getInt(ResponseBody.CODE)
                    val msg = jsonObj.getString(ResponseBody.MSG)
                    when (code){
                        0 -> {
                            mRequestCallCache.remove(key)
                            return JsonParseUtil.parseMediaStreamContent(jsonObj)
                        }
                        else ->{
                            _netError.postValue(NetError(code, msg))
                        }
                    }
                }
                catch (e: JSONException){
                    _netError.postValue(NetError(NetError.JSON_ERROR, e.message ?: "JSONException"))
                }
                catch (e: Exception){
                    _netError.postValue(NetError(NetError.OTHER_EXCEPTION, e.message ?: "Exception"))
                }
            } ?: kotlin.run {
                _netError.postValue(NetError(NetError.OTHER_EXCEPTION, "Http response body is null"))
            }
        }
        mRequestCallCache.remove(key)
        return StreamContent()
    }

    fun cancelRequest(key: String){
        mRequestCallCache[key]?.cancel()
    }

}