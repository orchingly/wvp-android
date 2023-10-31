package com.ly.wvp.data.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class DataStorage {

    companion object{
        private const val TAG = "DataStorage"
        private const val IP = "ip"
        private const val PORT = "port"
        private const val TLS = "tls"
    }
    private lateinit var mContext: Context
    private lateinit var sp: SharedPreferences

    constructor(context: Context){
        this.mContext = context.applicationContext
        sp = mContext.getSharedPreferences("wvp", MODE_PRIVATE)

    }

    fun saveConfig(config: SettingsConfig): Boolean{
        val ed = sp.edit()
        ed.putString(IP, config.ip)
        ed.putInt(PORT, config.port)
        ed.putBoolean(TLS, config.enableTls)
        return ed.commit()
    }

    fun getConfig(): SettingsConfig{
        return SettingsConfig(sp.getString(IP, "")?: "", sp.getInt(PORT, 0), sp.getBoolean(TLS, false))
    }

}