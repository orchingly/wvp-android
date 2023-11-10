package com.ly.wvp.device.onescreen

/**
 * 实体类,设备通道选项
 */
data class SelectionItem(val deviceId: String, val channelId: String){

    override fun equals(other: Any?): Boolean {

        return when (other) {
            null -> {
                false
            }
            is SelectionItem -> {
                other.deviceId == deviceId && other.channelId == channelId
            }
            else -> {
                false
            }
        }
    }

    override fun hashCode(): Int {
        var result = deviceId.hashCode()
        result = 31 * result + channelId.hashCode()
        return result
    }
}
