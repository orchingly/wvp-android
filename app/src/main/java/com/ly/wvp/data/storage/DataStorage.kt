package com.ly.wvp.data.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.ArrayMap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ly.wvp.data.model.Device
import com.ly.wvp.data.model.LoadDeviceChannel
import com.ly.wvp.device.onescreen.SelectionItem

class DataStorage(context: Context) {

    companion object {

        private const val TAG = "DataStorage"
        private const val IP = "ip"
        private const val PORT = "port"
        private const val TLS = "tls"
        private const val REMEMBER_PASSWD = "remember_passwd"
        private const val PASSWD = "passwd"
        private const val USER = "user"

        private const val MULTI_PLAY_DEVICE_SELECTION = "multi_play_device_selection"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: DataStorage? = null

        fun getInstance(context: Context): DataStorage {
            val i = instance
            if (i != null) {
                return i
            }

            return synchronized(this) {
                val i2 = instance
                if (i2 != null) {
                    i2
                } else {
                    val created = DataStorage(context)
                    instance = created
                    created
                }
            }
        }
    }

    private var mContext: Context
    private var sp: SharedPreferences

    private var mConfig: SettingsConfig? = null

    /**
     * 多设备同屏播放选中结果
     * 保存到文件
     */
    private var mSelectionsCache: ArrayList<SelectionItem>? = null

    /**
     * 设备和对应的通道列表缓存
     * 仅缓存,不保存到文件
     */
    private val mDeviceChannelCache = ArrayMap<Device, LoadDeviceChannel>()

    /**
     * Device缓存,用来快速查找Device, deviceId查找Device
     */
    private val mDeviceCache = ArrayMap<String, Device>()

    /**
     * 缓存device列表,服务器返回顺序排列
     */
    private val mDeviceCacheWithOrder = ArrayList<String>()

    init {
        this.mContext = context.applicationContext
        sp = mContext.getSharedPreferences("wvp", MODE_PRIVATE)
    }

    fun saveConfig(config: SettingsConfig): Boolean{
        val ed = sp.edit()
        ed.putString(IP, config.ip)
        ed.putInt(PORT, config.port)
        ed.putBoolean(TLS, config.enableTls)
        ed.putBoolean(REMEMBER_PASSWD, config.rememberPasswd)
        ed.putString(PASSWD, config.passwd)
        ed.putString(USER, config.user)
        val success = ed.commit()
        //缓存
        if (success){
            mConfig = config
        }
        return success
    }

    fun getConfig(): SettingsConfig{
        mConfig?.let {
            Log.d(TAG, "getConfig: return cached config")
            return it
        }?: kotlin.run {
            Log.d(TAG, "getConfig: read config")
            mConfig = SettingsConfig(sp.getString(IP, "") ?: "",
                sp.getInt(PORT, 0),
                sp.getBoolean(TLS, false), sp.getBoolean(REMEMBER_PASSWD, false),
                sp.getString(PASSWD, "")?: "",
                sp.getString(USER, "")?: "")
            return mConfig as SettingsConfig
        }
    }

    /**
     * 缓存Device
     */
    fun cacheDevice(device: Device){

        device.getDeviceId()?.let {
            //去重
            if (mDeviceCacheWithOrder.contains(it)){
                mDeviceCache.remove(it)
                mDeviceCacheWithOrder.remove(it)
            }
            mDeviceCache[it] = device
            mDeviceCacheWithOrder.add(it)
        }?:{
            Log.w(TAG, "cacheDevice: null device id $device")
        }
    }

    /**
     * 缓存Channel
     */
    fun cacheDeviceAndChannels(data: LoadDeviceChannel){
        val device = mDeviceCache[data.queryId()]
        device?.let {
            mDeviceChannelCache[it] = data
        }?: kotlin.run {
            Log.w(TAG, "cacheDeviceAndChannels: device not found, ${data.queryId()}")
        }
    }

    fun getDeviceList(): ArrayList<String>{
        return mDeviceCacheWithOrder
    }
    /**
     * @return 设备和通道
     */
    /*fun getDeviceChannelCache(): ArrayMap<Device, LoadDeviceChannel>{
        return mDeviceChannelCache
    }*/

    fun getChannelsByDeviceId(id: String): LoadDeviceChannel?{
        return mDeviceChannelCache[mDeviceCache[id]]
    }

    /**
     * 保存同屏播放的设备列表
     */
    fun saveMultiPlayDeviceList(selections: List<SelectionItem>): Boolean{
        //首次保存时创建缓存,后续同步更新,再次访问返回缓存
        if (mSelectionsCache == null){
            mSelectionsCache = ArrayList()
        }
        //清空
        if (selections.isEmpty()){
            mSelectionsCache?.clear()
            val ed = sp.edit()
            ed.remove(MULTI_PLAY_DEVICE_SELECTION)
            return ed.commit()
        }

        val gson = Gson()
        val json = gson.toJson(selections, ArrayList<SelectionItem>().javaClass)

        val ed = sp.edit()
        ed.putString(MULTI_PLAY_DEVICE_SELECTION, json)
        Log.d(TAG, "saveMultiPlayDeviceList: $json")
        val success = ed.commit()
        //缓存
        if (success){
            mSelectionsCache?.clear()
            mSelectionsCache?.addAll(selections)
        }
        return success
    }

    fun getMultiPlayDeviceList(): ArrayList<SelectionItem>{
        //缓存不为空,返回缓存
        mSelectionsCache?.let {
            return ArrayList(it)
        }
        val selectionJson = sp.getString(MULTI_PLAY_DEVICE_SELECTION, "")
        return if (!selectionJson.isNullOrEmpty()){
            Gson().fromJson(selectionJson, object :TypeToken<ArrayList<SelectionItem>>(){}.type)
        }else{
            ArrayList()
        }
    }


    private var cacheAlarmFilterArray: BooleanArray? = null

    /**
     * 传入报警类型:正常,移动...
     * 返回每个报警类型的选中状态
     */
    fun getAlarmFilterArray(alarm: Array<String>): BooleanArray {
        if (cacheAlarmFilterArray == null){
            cacheAlarmFilterArray = BooleanArray(alarm.size)
            for (i in alarm.indices){
                cacheAlarmFilterArray!![i] = sp.getBoolean(alarm[i], false)
            }
        }
        return cacheAlarmFilterArray!!
    }

    /**
     * 保存报警过滤选项选中状态
     * key:报警类型名称, value:状态
     */
    fun saveAlarmFilterArray(alarm: Array<String>, filter: BooleanArray){
        cacheAlarmFilterArray = filter.copyOf()
        val ed = sp.edit()
        for (i in alarm.indices){
            ed.putBoolean(alarm[i], filter[i])
        }
        ed.apply()
    }

}