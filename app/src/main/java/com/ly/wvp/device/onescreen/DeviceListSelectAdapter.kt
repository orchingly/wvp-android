package com.ly.wvp.device.onescreen

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ly.wvp.R
import com.ly.wvp.data.model.DeviceChanel

class DeviceListSelectAdapter(private val mContext: Context,
                              private val mSelection: ArrayList<SelectionItem>,
                              private val mDeviceList: List<String>,
                              private val mDeviceChannels: Map<String, List<DeviceChanel>>): Adapter<DeviceListSelectAdapter.DeviceHolder>() {

    companion object{
        const val TAG = "DeviceListSelectAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val holder = DeviceHolder(LayoutInflater.from(parent.context).inflate(R.layout.device_selector_holder, parent, false))
        return holder
    }

    override fun getItemCount(): Int {
        return mDeviceList.size
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {

        val deviceId = mDeviceList[position]
        //查找并绑定device列表的channel数据
        val channelList = ArrayList<String>()
        mDeviceChannels[deviceId]?.forEach{
            it.getChannelId()?.let {channel ->
                Log.d(TAG, "onBindViewHolder: device = $deviceId, channel = $channel")
                channelList.add(channel)
            }
        }
        val channelItemAdapter = ChannelItemAdapter(mContext, deviceId,  channelList, mSelection)
        holder.deviceId.text = deviceId
        holder.channelListView.adapter = channelItemAdapter
        channelItemAdapter.notifyDataSetChanged()
    }

    class DeviceHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val deviceId: TextView = itemView.findViewById(R.id.select_device_id)
        val channelListView: ListView = itemView.findViewById(R.id.select_item_channel_list)
    }

}