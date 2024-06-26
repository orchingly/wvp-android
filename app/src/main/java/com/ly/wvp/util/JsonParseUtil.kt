package com.ly.wvp.util

import android.util.ArrayMap
import android.util.Log
import com.ly.wvp.auth.ResponseBody.Companion.APP
import com.ly.wvp.auth.ResponseBody.Companion.DATA
import com.ly.wvp.auth.ResponseBody.Companion.HLS
import com.ly.wvp.auth.ResponseBody.Companion.HTTPS_HLS
import com.ly.wvp.auth.ResponseBody.Companion.HTTPS_PORT
import com.ly.wvp.auth.ResponseBody.Companion.ID
import com.ly.wvp.auth.ResponseBody.Companion.IP
import com.ly.wvp.auth.ResponseBody.Companion.LIST
import com.ly.wvp.auth.ResponseBody.Companion.PORT
import com.ly.wvp.auth.ResponseBody.Companion.RTMP
import com.ly.wvp.auth.ResponseBody.Companion.STREAM
import com.ly.wvp.auth.ResponseBody.Companion.STREAM_IP
import com.ly.wvp.auth.ResponseBody.Companion.TIME
import com.ly.wvp.calendar.Calendar
import com.ly.wvp.data.model.CloudRecordItem
import com.ly.wvp.data.model.Device
import com.ly.wvp.data.model.DeviceChanel
import com.ly.wvp.data.model.MediaServerItem
import com.ly.wvp.data.model.PageInfo
import com.ly.wvp.data.model.StreamContent
import com.ly.wvp.data.model.AlarmInfo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


const val TAG = "JsonParseUtil"

object JsonParseUtil {

    fun parseDevice(jsonObj: JSONObject): ArrayList<Device>{
        val deviceList = ArrayList<Device>()
        val deviceData = jsonObj.getJSONObject(DATA)
        val deviceArray = deviceData.getJSONArray(LIST)

        val length = deviceArray.length()
        for (i in 0 until length){
            val deviceObj = deviceArray.getJSONObject(i)
            val device = Device()
            device.setChannelCount(deviceObj.getInt("channelCount"))
            device.setCreateTime(deviceObj.getString("createTime"))
            device.setDeviceId(deviceObj.getString("deviceId"))
            try {
                device.setManufacturer(deviceObj.getString("manufacturer"))
            }
            catch (e: JSONException){
                Log.d(TAG, "parseDevice:json  manufacturer not found")
            }

            device.setOnLine(deviceObj.getBoolean("onLine"))
            device.setPort(deviceObj.getInt("port"))
            device.setStreamMode(deviceObj.getString("streamMode"))
            deviceList.add(device)
        }
        return deviceList
}

    fun parseDeviceChannel(jsonObj: JSONObject): ArrayList<DeviceChanel>{
        val channelList = ArrayList<DeviceChanel>()
        val deviceData = jsonObj.getJSONObject(DATA)
        val channelArray = deviceData.getJSONArray(LIST)

        val length = channelArray.length()
        for (i in 0 until length){
            val channelObj = channelArray.getJSONObject(i)
            val channel = DeviceChanel()
            channel.setChannelId(channelObj.getString("channelId"))
            channel.setDeviceId(channelObj.getString("deviceId"))
            channel.setName(channelObj.getString("name"))
            channel.setHasAudio(channelObj.getBoolean("hasAudio"))
            channel.setPTZType(channelObj.getInt("ptzType"))
            try {
                channel.setManufacture(channelObj.getString("manufacture"))
            }
            catch (e: JSONException){
                Log.d(TAG, "parseDeviceChannel: json no manufacture")
            }

            try {
                channel.setParentId(channelObj.getString("parentId"))
            }
            catch (e: JSONException){
                Log.d(TAG, "parseDeviceChannel: json no parentId")
            }

            channelList.add(channel)
        }
        return channelList
    }

    fun parseMediaStreamContent(jsonObj: JSONObject): StreamContent{
        //解析一个hls播放地址即可,其他字段暂时无用
        val data = jsonObj.getJSONObject(DATA)
        val content = StreamContent()
        content.setApp(data.getString(APP))
        content.setHls(data.getString(HLS))
        content.setHttps_hls(data.getString(HTTPS_HLS))
        content.setRtmp(data.getString(RTMP))
        return content
    }

    fun parseMediaServerList(jsonObj: JSONObject): ArrayList<MediaServerItem>{
        val data = jsonObj.getJSONArray(DATA)
        val serverList = ArrayList<MediaServerItem>()
        for (i in 0 until data.length()){
            val mediaId = data.getJSONObject(i).getString(ID)
            val item = MediaServerItem()
            item.id = mediaId
            item.streamIp = data.getJSONObject(i).getString(STREAM_IP)
            item.ip = data.getJSONObject(i).getString(IP)
            item.httpPort = data.getJSONObject(i).getInt(PORT)
            item.httpSSlPort = data.getJSONObject(i).getInt(HTTPS_PORT)
            serverList.add(item)
        }
        return serverList
    }

    fun parseCloudRecordList(jsonObj: JSONObject): PageInfo<Map<String, String>>{
        val data = jsonObj.getJSONObject(DATA)
        val recordInfo = PageInfo<Map<String, String>>()
        //页码信息
        try {
            val pageNum = data.getInt("pageNum")
            val pageSize = data.getInt("pageSize")
            val size = data.getInt("size")
            val pages = data.getInt("pages")
            val total = data.getInt("total")
            recordInfo.pageNum = pageNum
            recordInfo.pageSize = pageSize
            recordInfo.size = size
            recordInfo.pages = pages
            recordInfo.total = total
        }
        catch (e: JSONException){
            Log.e(TAG, "parseCloudRecordList: json页码解析错误")
        }
        val recordArray = data.getJSONArray(LIST)
        recordInfo.list = ArrayList()
        for (i in 0 until recordArray.length()){
            val recordObj = recordArray.getJSONObject(i)
            val app = recordObj.getString(APP)
            val stream = recordObj.getString(STREAM)
            val time = recordObj.getString(TIME)

            val map = ArrayMap<String, String>()
            map[APP] = app
            map[STREAM] = stream
            map[TIME] = time
            (recordInfo.list as ArrayList<Map<String, String>>).add(map)
        }
        return recordInfo
    }


    fun parseRecordDate(jsonArray: JSONArray): ArrayList<Calendar>{
        val dateList = ArrayList<Calendar>()
        for (i in 0 until jsonArray.length()){
            val calendar = Calendar()
            //"2023-09-25"
            val time = jsonArray.getString(i)
            val s = time.split("-")
            if (s.size != 3){
                Log.w(TAG, "parseRecordDate: date parse error, origin: $time" )
                return dateList
            }

            try {
                calendar.year = s[0].toInt()
                calendar.month = s[1].toInt()
                calendar.day = s[2].toInt()
            }
            catch (e: NumberFormatException){
                Log.e(TAG, "parseRecordDate: NumberFormatException: ${e.message}, origin: $time " )
                return dateList
            }
            dateList.add(calendar)
        }
        return dateList
    }


    fun parseRecordFileList(jsonObj: JSONObject): ArrayList<CloudRecordItem>{
        val jsonArray = jsonObj.getJSONArray(LIST)
        val recordList = ArrayList<CloudRecordItem>()
        for (i in 0 until jsonArray.length()){
            //TODO: jsonArray.getString(i) -> object
            //            {
            //                "id": 709,
            //                "app": "rtp",
            //                "stream": "34020000001320000002_34020000001320000002",
            //                "callId": "0b334b0f7a11b0e9f7f24d0a5c9e008d@0.0.0.0",
            //                "startTime": 1715615842000,
            //                "endTime": 1715616142000,
            //                "mediaServerId": "1010101010100",
            //                "fileName": "23-57-22-4.mp4",
            //                "filePath": "/data/ipcamera/ZLMedia/www/record/rtp/34020000001320000002_34020000001320000002/2024-05-13/23-57-22-4.mp4",
            //                "folder": "/data/ipcamera/ZLMedia/www/record/rtp/34020000001320000002_34020000001320000002/",
            //                "collect": false,
            //                "reserve": null,
            //                "fileSize": 3248200,
            //                "timeLen": 300000
            //            },
            val cloudRecordItemObj = jsonArray.getJSONObject(i)
            val recordItem = CloudRecordItem()
            recordItem.id = cloudRecordItemObj.getInt("id")
            recordItem.app = cloudRecordItemObj.getString("app")
            recordItem.stream = cloudRecordItemObj.getString("stream")
            recordItem.startTime = cloudRecordItemObj.getLong("startTime")
            recordItem.endTime = cloudRecordItemObj.getLong("endTime")
            recordItem.fileName = cloudRecordItemObj.getString("fileName")
            recordItem.timeLen = cloudRecordItemObj.getLong("timeLen")
            recordList.add(recordItem)
        }
        return recordList
    }

    fun parseRecordActionList(jsonArray: JSONArray): ArrayList<AlarmInfo>{
        val actionList = ArrayList<AlarmInfo>()
        for (i in 0 until jsonArray.length()){
            val action = AlarmInfo()
            val obj = jsonArray.getJSONObject(i)
            action.app = obj.getString(AlarmInfo.app)
            action.stream = obj.getString(AlarmInfo.stream)
            action.alarmType = obj.getInt(AlarmInfo.DType)
            action.actTime = obj.getString(AlarmInfo.actTime)
            action.actFlag = obj.getInt(AlarmInfo.actType)
            actionList.add(action)
        }
        return actionList
    }
}