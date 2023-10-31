package com.ly.wvp.data.model

data class LoadDeviceChannel(private val deviceId: String, private val channels: List<DeviceChanel>) {
    /**
     * 发起查询的deviceId
     */
    fun queryId() = deviceId

    /**
     * Channel查询结果
     */
    fun channels() = channels
}