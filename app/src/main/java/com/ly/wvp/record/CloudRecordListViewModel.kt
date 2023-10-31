package com.ly.wvp.record

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.wvp.auth.HttpConnectionClient
import com.ly.wvp.auth.NetError
import com.ly.wvp.auth.NetError.Companion.OK
import com.ly.wvp.auth.ResponseBody
import com.ly.wvp.auth.ServerUrl
import com.ly.wvp.data.model.LoadDeviceChannel
import com.ly.wvp.data.model.MediaServerItem
import com.ly.wvp.data.model.PageInfo
import com.ly.wvp.data.storage.SettingsConfig
import com.ly.wvp.util.JsonParseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 *
 * 请求app+stream录像列表
 * "http://localhost:18080/record_proxy/FQ3TF8yT83wh5Wvz/api/record/list?page=1&count=15"
 *
 * 请求server列表
 * /record_proxy/FQ3TF8yT83wh5Wvz/api/record/date/list
 *
 */
class CloudRecordListViewModel : ViewModel() {
    companion object{
        private const val TAG = "CloudRecordListViewModel"
    }

    private var _mediaServerList = MutableLiveData<List<MediaServerItem>>()

    private var _cloudRecordPageInfo = MutableLiveData<PageInfo<Map<String, String>>>()

    private var _netError = MutableLiveData<NetError>()

    private var config: SettingsConfig? = null

    fun getMediaServerLiveData(): LiveData<List<MediaServerItem>> =  _mediaServerList

    fun getCloudRecordPageInfo(): LiveData<PageInfo<Map<String, String>>> = _cloudRecordPageInfo

    fun getError(): LiveData<NetError> = _netError

    fun setConfig(config: SettingsConfig){
        this.config = config
    }



    fun requestCloudRecord(mediaServerId: String){
        val page = "1"
        val count = "15"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val recordInfo = loadCloudRecordForServerPage(mediaServerId, page, count)
                launch(Dispatchers.Main){
                    _cloudRecordPageInfo.value = recordInfo
                }
            }
            catch (e: IOException){
                Log.w(TAG, "requestCloudRecord: error ${e.message}" )
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

    private fun loadCloudRecordForServerPage(mediaServerId: String, page: String, count: String): PageInfo<Map<String, String>>{
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.CLOUD_PROXY)
            .addPathSegment(mediaServerId)
            .addPathSegments(ServerUrl.API_RECORD)
            .addPathSegment(ServerUrl.LIST)
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
                    val msg = jsonObj.getString(ResponseBody.MSG)
                    when (code){
                        0 -> {
                            return JsonParseUtil.parseCloudRecordList(jsonObj)
                        }
                        else ->{
                            _netError.postValue(NetError(code, msg))
                        }
                    }
                }
                catch (e: JSONException){
                    //wvp-assist未启动/未配置返回结果为空状态200
                    if (this.code == OK && it.string().isEmpty()){
                        _netError.postValue(NetError(NetError.JSON_ERROR, "请检查wvp-assist"))
                    }
                    else{
                        _netError.postValue(NetError(NetError.JSON_ERROR, e.message ?: "JSONException"))
                    }
                }
                catch (e: Exception){
                    _netError.postValue(NetError(NetError.OTHER_EXCEPTION, e.message ?: "Exception"))
                }
            } ?: kotlin.run {
                _netError.postValue(NetError(NetError.OTHER_EXCEPTION, "Http response body is null"))
            }
        }
        return PageInfo()
    }

    fun requestRemoteMediaServer(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val serverList = loadMediaServer()
                launch(Dispatchers.Main){
                    _mediaServerList.value = serverList
                }
            }
            catch (e: IOException){
                Log.w(TAG, "requestRemoteMediaServer: error ${e.message}" )
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

    private fun loadMediaServer(): ArrayList<MediaServerItem>{
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.API_SERVER)
            .addPathSegments(ServerUrl.MEDIA_ONLINE)
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
                    val msg = jsonObj.getString(ResponseBody.MSG)
                    when (code){
                        0 -> {
                            return JsonParseUtil.parseMediaServerList(jsonObj)
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
        return ArrayList()
    }

}