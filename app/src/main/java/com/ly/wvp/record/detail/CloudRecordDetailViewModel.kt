package com.ly.wvp.record.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.wvp.auth.HttpConnectionClient
import com.ly.wvp.auth.NetError
import com.ly.wvp.auth.ResponseBody
import com.ly.wvp.auth.ResponseBody.Companion.APP
import com.ly.wvp.auth.ResponseBody.Companion.CODE
import com.ly.wvp.auth.ResponseBody.Companion.DATA
import com.ly.wvp.auth.ResponseBody.Companion.MSG
import com.ly.wvp.auth.ResponseBody.Companion.STREAM
import com.ly.wvp.auth.ServerUrl
import com.ly.wvp.calendar.Calendar
import com.ly.wvp.data.model.CloudRecordItem
import com.ly.wvp.data.model.StreamDetectionItem
import com.ly.wvp.data.storage.SettingsConfig
import com.ly.wvp.util.JsonParseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
* 有记录的日期
* /record_proxy/FQ3TF8yT83wh5Wvz/api/record/date/list?app=rtp&stream=43000000801320000009_43000000801310000009
* {
    *     "code": 0,
    *     "msg": "成功",
    *     "data": [
    *         "2023-09-25",
    *         "2023-09-26",
    *         "2023-09-27",
    *         "2023-09-28",
    *         "2023-10-07"
    *     ]
    * }
*
* 请求云端记录文件列表
* record_proxy/FQ3TF8yT83wh5Wvz/api/record/file/list?app=rtp&stream=43000000801320000009_43000000801310000009&startTime=2023-10-07+00:00:00&endTime=2023-10-07+23:59:59&page=1&count=1000000
* {
    *     "code": 0,
    *     "msg": "成功",
    *     "data": {
        *         "pageNum": 1,
        *         "pageSize": 1000000,
        *         "size": 47,
        *         "pages": 1,
        *         "total": 47,
        *         "list": [
        *             "10:09:42-10:11:40-118514.mp4",
        *             "10:13:10-10:15:10-120685.mp4",
        *             "10:15:10-10:17:10-120719.mp4",
        *             "10:17:11-10:17:21-10987.mp4",
        *             "10:21:20-10:23:19-119402.mp4",
        *             "10:24:22-10:25:24-62101.mp4",
        *             "10:26:10-10:26:41-31278.mp4",
        *             "10:26:47-10:27:02-15342.mp4",
        *             "10:28:48-10:30:48-120556.mp4",
        *             "10:30:49-10:30:57-8054.mp4",
        *             "10:42:58-10:44:12-74654.mp4",
        *             "10:44:22-10:44:50-28642.mp4",
        *             "10:46:58-10:47:54-56073.mp4",
        *             "10:48:04-10:48:33-29194.mp4",
        *             "10:50:16-10:50:53-37654.mp4",
        *             "11:07:00-11:09:00-120004.mp4",
        *             "11:09:00-11:11:00-120833.mp4",
        *             "11:11:00-11:12:23-83532.mp4",
        *             "11:28:23-11:28:40-17913.mp4",
        *             "11:28:44-11:29:40-56265.mp4",
        *             "11:30:57-11:32:57-120309.mp4",
        *             "11:32:58-11:33:49-51648.mp4",
        *             "11:55:47-11:57:47-120495.mp4",
        *             "11:57:47-11:58:50-63029.mp4",
        *             "11:59:08-12:01:08-120470.mp4",
        *             "12:01:09-12:01:10-1911.mp4",
        *             "12:03:27-12:03:53-26077.mp4",
        *             "14:13:30-14:15:30-120688.mp4",
        *             "14:15:31-14:17:31-120513.mp4",
        *             "14:17:31-14:18:23-52629.mp4",
        *             "14:18:45-14:19:03-18759.mp4",
        *             "14:19:20-14:20:39-79503.mp4",
        *             "14:21:37-14:22:12-35714.mp4",
        *             "14:31:38-14:32:17-39669.mp4",
        *             "14:33:46-14:34:15-29927.mp4",
        *             "14:40:35-14:42:35-120257.mp4",
        *             "14:42:35-14:42:48-13517.mp4",
        *             "14:44:08-14:44:35-27156.mp4",
        *             "14:56:18-14:56:42-24604.mp4",
        *             "14:57:46-14:58:31-45835.mp4",
        *             "15:19:20-15:19:58-38030.mp4",
        *             "15:20:14-15:22:14-120901.mp4",
        *             "15:22:14-15:23:23-69723.mp4",
        *             "15:25:40-15:25:57-17535.mp4",
        *             "15:26:07-15:26:50-43299.mp4",
        *             "15:36:03-15:37:11-68986.mp4",
        *             "15:41:56-15:43:00-64257.mp4"
        *         ]
        *     }
    * }
*/
class CloudRecordDetailViewModel : ViewModel() {
    companion object{
        private const val TAG = "CloudRecordDetailViewModel"
    }

    private var _recordCalendar = MutableLiveData<List<Calendar>>()

    private var _recordFileList = MutableLiveData<List<CloudRecord>>()

    private var _recordAction = MutableLiveData<List<StreamDetectionItem>>()

    private var _netError = MutableLiveData<NetError>()

    /**
     * 请求云端录像播放url的数据流，每次点击列表出发请求，收到返回的url后将其写入Flow
     * 外部监听FLOW,有数据就开始播放， 策略：DROP_OLDEST
     */
    private val _playUrlFlow = MutableSharedFlow<String>(extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun getPlayUrlFlow(): Flow<String> = _playUrlFlow

    private var config: SettingsConfig? = null

    fun getRecordCalendar(): LiveData<List<Calendar>> =  _recordCalendar

    fun getRecordFileList(): LiveData<List<CloudRecord>> = _recordFileList

    fun getRecordActionList(): LiveData<List<StreamDetectionItem>> = _recordAction

    fun getError(): LiveData<NetError> = _netError

    fun setConfig(config: SettingsConfig){
        this.config = config
    }

    fun requestRecordFileList(calendar:Calendar, app: String, stream: String){

        val yy = calendar.year.toString()
        val mm = if (calendar.month < 10) "0${calendar.month}" else calendar.month.toString()
        val dd = if (calendar.day < 10) "0${calendar.day}" else calendar.day.toString()

        val startTime = "$yy-$mm-$dd 00:00:00"
        val endTime = "$yy-$mm-$dd 23:59:59"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cloudRecordList = loadRecordFileList(app, stream, startTime, endTime)
                launch(Dispatchers.Main){
                    val list = ArrayList<CloudRecord>()
                    cloudRecordList.forEach{
                        list.add(CloudRecord(app, stream, calendar, it.fileName ?: "", recordItem = it))
                    }
                    _recordFileList.value = list
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

    private fun loadRecordFileList(app: String, stream: String, startTime: String, endTime: String): ArrayList<CloudRecordItem>{
        //当天的记录一页加载完
        //原始请求url:http://192.168.200.2:18080/api/cloud/record/list?app=rtp&stream=34020000001320000002_34020000001320000002&startTime=2024-05-13 00:00:00&endTime=2024-05-13 23:59:59&page=1&count=1000000
        val page = "1"
        val count = "1000000"
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.API_CLOUD_RECORD)
//            .addPathSegment(mediaServer)
//            .addPathSegments(ServerUrl.DATE)
//            .addPathSegment(ServerUrl.FILE)
            .addPathSegment(ServerUrl.LIST)
            .addQueryParameter(APP, app)
            .addQueryParameter(STREAM, stream)
            .addQueryParameter(ServerUrl.START_TIME, startTime)
            .addQueryParameter(ServerUrl.END_TIME, endTime)
            .addQueryParameter(ServerUrl.PARAM_KEY_COUNT, count)
            .addQueryParameter(ServerUrl.PARAM_KEY_PAGE, page)
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
                            return JsonParseUtil.parseRecordFileList(jsonObj.getJSONObject(DATA))
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

    fun requestRecordCalendar(app: String, stream: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val recordInfo = loadRecordCalendar(app, stream)
                launch(Dispatchers.Main){
                    _recordCalendar.value = recordInfo
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

    /**
     * 查询有录像记录的日期
     */
    private fun loadRecordCalendar(app: String, stream: String): ArrayList<Calendar>{
        //原始请求url: //new api: http://192.168.200.2:18080/api/cloud/record/date/list?app=rtp&stream=34020000001320000002_34020000001320000002&year=2024&month=5
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.API_CLOUD_RECORD)
//            .addPathSegment(mediaServer)
//            .addPathSegments(ServerUrl.API_RECORD)
            .addPathSegment(ServerUrl.DATE)
            .addPathSegment(ServerUrl.LIST)
            .addQueryParameter(APP, app)
            .addQueryParameter(STREAM, stream)
            //TODO:按月查询
            .addQueryParameter("year", "2024")
            .addQueryParameter("month", "5")
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()
        HttpConnectionClient.request(request).run {
            this.body?.let {
                try {
                    /*
                    {"code":0,"msg":"成功","data":["2024-05-14","2024-05-12","2024-05-13"]}
                     */
                    val jsonObj = JSONObject(it.string())
                    val code = jsonObj.getInt(ResponseBody.CODE)
                    val msg = jsonObj.getString(ResponseBody.MSG)
                    when (code){
                        0 -> {
                            return JsonParseUtil.parseRecordDate(jsonObj.getJSONArray(DATA))
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

    fun requestAction(calendar:Calendar, app: String, stream: String){
        val yy = calendar.year.toString()
        val mm = if (calendar.month < 10) "0${calendar.month}" else calendar.month.toString()
        val dd = if (calendar.day < 10) "0${calendar.day}" else calendar.day.toString()

        val startTime = "$yy-$mm-$dd 00:00:00"
        val endTime = "$yy-$mm-$dd 23:59:59"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val actionList = loadActionList(app, stream, startTime, endTime)
                launch(Dispatchers.Main){
                    _recordAction.value = actionList
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

    /**
     * http://localhost:18080/api/record/action/list?app=rtp&stream=43000000801320000008_43000000801310000008&startTime=2023-09-18%2000:00:00&endTime=2023-09-18%2023:59:59
     *
     */
    private fun loadActionList(app: String, stream: String, startTime: String, endTime: String): ArrayList<StreamDetectionItem>{
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.RECORD_ACTION)
            .addPathSegment(ServerUrl.LIST)
            .addQueryParameter(APP, app)
            .addQueryParameter(STREAM, stream)
            .addQueryParameter(ServerUrl.START_TIME, startTime)
            .addQueryParameter(ServerUrl.END_TIME, endTime)
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()
        HttpConnectionClient.request(request).run {
            this.body?.let {
                try {
                    val jsonObj = JSONObject(it.string())
                    val code = jsonObj.getInt(CODE)
                    val msg = jsonObj.getString(MSG)
                    //404
                    val notFound = checkNotFound(jsonObj)
                    if (notFound){
                        Log.d(TAG, "loadActionList: 404 not found loadActionList")
                        return ArrayList()
                    }
                    when (code){
                        0 -> {
                            return JsonParseUtil.parseRecordActionList(jsonObj.getJSONArray(DATA))
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

    private fun checkNotFound(jsonObj: JSONObject): Boolean {
        val obj = jsonObj.get(DATA)
        //正常返回JsonArray,异常返回JSONObject包含状态码
        if (obj is JSONObject){
            val status = obj.getInt("status")
            val msg = "Action list ${obj.getString("error")}"
            _netError.postValue(NetError(status,  msg))
            return true
        }
        return false
    }

    fun getPlayUrl(record: CloudRecord) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _playUrlFlow.emit(requestPlayUrl(record.recordItem.id))
            } catch (e: Exception) {
                _playUrlFlow.emit(e.message ?: "")
                _netError.postValue(NetError(NetError.OTHER_EXCEPTION, e.message ?: ""))
            }
        }
    }

    private fun requestPlayUrl(id: Int): String{
        //http://192.168.200.2:18080/api/cloud/record/play/path?recordId=709
        val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
            .addPathSegments(ServerUrl.API_CLOUD_RECORD)
            .addPathSegments(ServerUrl.PLAY_PATH)
            .addQueryParameter(ServerUrl.RECORD_ID, id.toString())
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()
        HttpConnectionClient.request(request).run {
            this.body?.let {
                try {
                    val jsonObj = JSONObject(it.string())
                    val code = jsonObj.getInt(CODE)
                    val msg = jsonObj.getString(MSG)
                    when (code) {
                        0 -> {
                            val pathObj = jsonObj.getJSONObject("data")
                            val httpPath = pathObj.getString("httpPath")
                            val httpsPath = pathObj.getString("httpsPath")
                            return if (config!!.enableTls) {
                                httpsPath
                            } else httpPath
                        }
                        else -> {
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
        return ""
    }


}