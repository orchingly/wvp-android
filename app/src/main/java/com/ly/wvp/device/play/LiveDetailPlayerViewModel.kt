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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LiveDetailPlayerViewModel : ViewModel() {

    companion object{
        private const val TAG = "LiveDetailPlayerViewModel"
        const val PTZ_UP = 1
        const val PTZ_DOWN = 2
        const val PTZ_LEFT = 3
        const val PTZ_RIGHT = 4
        const val PTZ_STOP = -1
        const val PTZ_UP_STR = "up"
        const val PTZ_DOWN_STR = "down"
        const val PTZ_LEFT_STR = "left"
        const val PTZ_RIGHT_STR = "right"
        const val PTZ_MOVE_STOP = "stop"

    }


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

    fun requestPtzMove(direction: Int, deviceId: String, channelId: String) {
        val command = when (direction){
            PTZ_LEFT -> PTZ_LEFT_STR
            PTZ_UP -> PTZ_UP_STR
            PTZ_RIGHT -> PTZ_RIGHT_STR
            PTZ_DOWN -> PTZ_DOWN_STR
            else -> PTZ_MOVE_STOP
        }
        if (command.isEmpty()){
            Log.w(TAG, "requestPtzMove: invalid command $direction")
            return
        }
        CoroutineScope(IO).launch {
            try {
                //stop命令延迟500,更好的移动效果
                if (command == PTZ_MOVE_STOP){
                    delay(300)
                }
                executePtzMove(command, deviceId, channelId)
            }
            catch (e: IOException){
                Log.w(TAG, "requestPtzMove: error ${e.message}" )
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

    //api/ptz/control/34020000001320000002/34020000001320000002?command=up&horizonSpeed=30&verticalSpeed=30&zoomSpeed=30
    //api/ptz/control/34020000001320000002/34020000001320000002?command=stop&horizonSpeed=30&verticalSpeed=30&zoomSpeed=30
    //api/ptz/control/34020000001320000002/34020000001320000002?command=left&horizonSpeed=30&verticalSpeed=30&zoomSpeed=30
    //api/ptz/control/34020000001320000002/34020000001320000002?command=stop&horizonSpeed=30&verticalSpeed=30&zoomSpeed=30
    //api/ptz/control/34020000001320000002/34020000001320000002?command=right&horizonSpeed=30&verticalSpeed=30&zoomSpeed=30
    //api/ptz/control/34020000001320000002/34020000001320000002?command=stop&horizonSpeed=30&verticalSpeed=30&zoomSpeed=30
    //api/ptz/control/34020000001320000002/34020000001320000002?command=down&horizonSpeed=30&verticalSpeed=30&zoomSpeed=30
    //api/ptz/control/34020000001320000002/34020000001320000002?command=stop&horizonSpeed=30&verticalSpeed=30&zoomSpeed=30
    private fun executePtzMove(command: String, deviceId: String, channelId: String){

        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.PTZ_CONTROL)
            .addPathSegment(deviceId)
            .addPathSegment(channelId)
            .addQueryParameter("command", command)
            .addQueryParameter("horizonSpeed", "100")
            .addQueryParameter("verticalSpeed", "100")
            .addQueryParameter("zoomSpeed", "100")
            .build()
        val body = FormBody.Builder().build()
        val request = Request.Builder()
            .url(httpUrl)
            .post(body)
            .build()
        Log.d(TAG, "executePtzMove: ${httpUrl.toUrl()}")
        HttpConnectionClient.request(request)
    }


}