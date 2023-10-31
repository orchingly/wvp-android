package com.ly.wvp.device.play

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.wvp.auth.HttpConnectionClient
import com.ly.wvp.auth.NetError
import com.ly.wvp.auth.ResponseBody
import com.ly.wvp.auth.ServerUrl
import com.ly.wvp.data.model.StreamContent
import com.ly.wvp.data.storage.SettingsConfig
import com.ly.wvp.util.JsonParseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LiveDetailPlayerViewModel : ViewModel() {

    companion object{
        private const val TAG = "LiveDetailPlayerViewModel"
    }

    private val _mediaStream = MutableLiveData<String>()

    private var _netError = MutableLiveData<NetError>()

    private var _streamContent = MutableLiveData<StreamContent>()

    private var config: SettingsConfig? = null

    fun getStream(): LiveData<StreamContent> = _streamContent

    fun getNetError(): LiveData<NetError> = _netError

    fun setConfig(config: SettingsConfig){
        this.config = config
    }

    /**
     * 发起点播
     */
    private fun requestPlay(deviceId: String, channelId: String): StreamContent{
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
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
            HttpConnectionClient.request(request).run {
                this.body?.let {
                    try {
                        val jsonObj = JSONObject(it.string())
                        val code = jsonObj.getInt(ResponseBody.CODE)
                        val msg = jsonObj.getString(ResponseBody.MSG)
                        when (code){
                            0 -> {
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
        return StreamContent()
    }


    fun requestStream(deviceId: String, channelId: String){
        CoroutineScope(IO).launch {
            try {
                val streamContent = requestPlay(deviceId, channelId)
                launch(Main){
                    _streamContent.value = streamContent
                }
            }
            catch (e: IOException){
                Log.w(TAG, "startPlay: error ${e.message}" )
                _netError.postValue( NetError(NetError.APP_ERROR, e.message?:"IOException"))
            }
            catch (e: IllegalStateException){
                _netError.postValue(NetError(NetError.APP_ERROR, e.message?:"IllegalStateException"))
            }
            catch (e: Exception){
                _netError.postValue(NetError(NetError.INTERNET_INVALID, e.message?:"Exception"))
            }

        }
    }


}