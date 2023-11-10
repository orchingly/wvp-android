package com.ly.wvp.device

import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.ly.wvp.R
import com.ly.wvp.data.model.Device
import com.ly.wvp.data.model.DeviceChanel

class DeviceAdapter : Adapter<DeviceHolder>() {

    companion object{
        private const val TAG = "DeviceAdapter"
    }

    private var deviceList: List<Device>? = null

    fun setDeviceList(deviceList: List<Device>){
        this.deviceList = deviceList
    }

    /**
     * 设置Channel
     * @return 列表的中的位置索引 -1:列表中未找到device
     */
    fun setDeviceChannelList(deviceId: String, channelList: List<DeviceChanel>): Int{
        deviceList?.let {
            for (i in it.indices){
                if (deviceId == it[i].getDeviceId()){
                    Log.d(TAG, "setDeviceChannelList: for channel $deviceId")
                    it[i].setChannelList(channelList)
                    return i
                }
            }
            if (it.isNotEmpty()){
                Log.w(TAG, "setDeviceChannelList: failed, deviceId {${channelList[0].getDeviceId()}} not found in device list" )
            }
            else{
                Log.w(TAG, "setDeviceChannelList: empty device list")
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val navController = parent.findNavController()
        return DeviceHolder(navController, LayoutInflater.from(parent.context).inflate(R.layout.view_holder_device_item, parent, false))
    }

    override fun getItemCount(): Int {
        return deviceList?.size ?: 0
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        //设备列表为null,表示网络加载数据未返回
        //非null则数据已返回,也可能是个空列表
        Log.d(TAG, "onBindViewHolder: ")
        val context = holder.itemView.context
        deviceList?.let {
            val device = it[position]
            holder.contentGroup.visibility = VISIBLE
            //通道信息延迟加载
            val list = device.getChannelList()
            list?.let { channels ->
                if (channels.isEmpty()){
                    Log.d(TAG, "onBindViewHolder: empty channels")
                    //加载到空数据,loading 消失,list隐藏
                    holder.channelLoading.visibility = GONE
                    holder.channelList.visibility = GONE
                    holder.channelLost.visibility = VISIBLE
                }
                else{
                    Log.d(TAG, "onBindViewHolder: has channels")
                    holder.channelLoading.visibility = GONE
                    holder.channelList.visibility = VISIBLE
                    holder.channelLost.visibility = GONE
                }
                holder.setChannelList(channels)
                //空,还未加载数据,显示loading
            }?: {
                Log.d(TAG, "onBindViewHolder: null channels")
                holder.channelLoading.visibility = VISIBLE
                holder.channelList.visibility = GONE
                holder.channelLost.visibility = GONE
            }

            holder.setDeviceId(device.getDeviceId())
            holder.deviceId.text = device.getDeviceId()
            holder.tcpTransport.text = device.getStreamMode()
            holder.deviceOnline.text = if (device.isOnLine())  "在线" else "离线"
            val onLineColor = if (device.isOnLine()) context.getColor(R.color.ticket_check_bg)  else context.getColor(R.color.text_secondary)
            holder.deviceOnline.setTextColor(onLineColor)
        } ?: {
            Log.e(TAG, "onBindViewHolder: deviceList is null")
        }
    }
}