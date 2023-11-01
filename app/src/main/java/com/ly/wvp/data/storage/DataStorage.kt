package com.ly.wvp.data.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log

class DataStorage(context: Context) {

    companion object {

        private const val TAG = "DataStorage"
        private const val IP = "ip"
        private const val PORT = "port"
        private const val TLS = "tls"

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

    init {
        this.mContext = context.applicationContext
        sp = mContext.getSharedPreferences("wvp", MODE_PRIVATE)
    }

    fun saveConfig(config: SettingsConfig): Boolean{
        val ed = sp.edit()
        ed.putString(IP, config.ip)
        ed.putInt(PORT, config.port)
        ed.putBoolean(TLS, config.enableTls)
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
            mConfig = SettingsConfig(sp.getString(IP, "") ?: "", sp.getInt(PORT, 0), sp.getBoolean(TLS, false))
            return mConfig as SettingsConfig
        }
    }

}