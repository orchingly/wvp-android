package com.ly.wvp.device

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.wvp.auth.HttpConnectionClient
import com.ly.wvp.auth.NetError
import com.ly.wvp.auth.ResponseBody
import com.ly.wvp.auth.ResponseBody.Companion.MSG
import com.ly.wvp.auth.ServerUrl
import com.ly.wvp.auth.TokenSession
import com.ly.wvp.data.model.Device
import com.ly.wvp.data.model.DeviceChanel
import com.ly.wvp.data.model.LoadDeviceChannel
import com.ly.wvp.data.storage.DataStorage
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

class DeviceListViewModel : ViewModel() {
    companion object{
        private const val TAG = "DeviceListViewModel"
    }

    private var _deviceList = MutableLiveData<List<Device>>()

    private var _channelList = MutableLiveData<LoadDeviceChannel>()

    private var _netError = MutableLiveData<NetError>()

    private var config: SettingsConfig? = null

    fun getDeviceLiveData(): LiveData<List<Device>> =  _deviceList

    fun getChannelList(): LiveData<LoadDeviceChannel> = _channelList

    fun getError(): LiveData<NetError> = _netError

    fun setConfig(config: SettingsConfig){
        this.config = config
    }

    /**
     * {
     *     "code": 401,
     *     "msg": "请登录后重新请求"
     * }
     */
    fun loadDeviceList(){
        CoroutineScope(IO).launch {
            try {
                val page = "1"
                val count = "15"
                val deviceList = queryAllDevices(page, count)
                launch(Main){
                    _deviceList.value = deviceList
                }
            }
            catch (e: IOException){
                Log.w(TAG, "loadDeviceList: error ${e.message}" )
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

    fun loadDeviceChannelList(deviceId: String){
        Log.d(TAG, "loadDeviceChannelList: for $deviceId")
        CoroutineScope(IO).launch {
            try {
                val page = "1"
                val count = "15"
                val chanelList = queryChanel(deviceId, page, count)
                Log.d(TAG, "loadDeviceChannelList returned : $deviceId")
                //连续postValue可能会丢失, 切到main再用value
                launch(Main){
                    _channelList.value = LoadDeviceChannel(deviceId, chanelList)
                }
            }
            catch (e: Exception){
                Log.w(TAG, "loadDeviceChannelList: error ${e.message}" )
                _netError.postValue(NetError(NetError.APP_ERROR, e.message ?: "JSONException"))
            }
        }
    }

    private fun queryChanel(deviceId: String, page: String, count: String): List<DeviceChanel>{
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.API_DEVICE_QUERY)
            .addPathSegment(ServerUrl.QUERY_DEVICES)
            .addPathSegment(deviceId)
            .addPathSegment(ServerUrl.QUERY_CHANNELS)
            .addQueryParameter(ServerUrl.PARAM_KEY_PAGE, page)
            .addQueryParameter(ServerUrl.PARAM_KEY_COUNT, count)
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()

        HttpConnectionClient.request(request).run {
            this.body?.let {
                try {
                    val jsonObj = JSONObject(it.string())
                    val code = jsonObj.getInt(ResponseBody.CODE)
                    val msg = jsonObj.getString(MSG)
                    when (code){
                        0 -> {
                            return JsonParseUtil.parseDeviceChannel(jsonObj)
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

            return ArrayList()
        }
    }

    private fun queryAllDevices(page: String, count: String): List<Device>{
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.API_DEVICE_QUERY)
            .addPathSegment(ServerUrl.QUERY_DEVICES)
            .addQueryParameter(ServerUrl.PARAM_KEY_PAGE, page)
            .addQueryParameter(ServerUrl.PARAM_KEY_COUNT, count)
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()

        HttpConnectionClient.request(request).run {
            this.body?.let {
                try {
                    val jsonObj = JSONObject(it.string())
                    val code = jsonObj.getInt(ResponseBody.CODE)
                    val msg = jsonObj.getString(MSG)
                    when (code){
                        0 -> {
                            return JsonParseUtil.parseDevice(jsonObj)
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

            return ArrayList()
        }
    }

}