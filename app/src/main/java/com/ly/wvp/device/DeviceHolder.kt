package com.ly.wvp.device

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.ly.wvp.R
import com.ly.wvp.data.model.DeviceChanel
import com.ly.wvp.device.channel.ChannelAdapter

class DeviceHolder(navController: NavController, itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object{
        private const val TAG = "DeviceHolder"
    }

    val deviceId = itemView.findViewById<TextView>(R.id.device_id)
    val tcpTransport = itemView.findViewById<TextView>(R.id.device_tcp_transport)
    val deviceOnline = itemView.findViewById<TextView>(R.id.device_online_state)
    val channelLoading = itemView.findViewById<ProgressBar>(R.id.device_channel_loading)
    val contentGroup = itemView.findViewById<Group>(R.id.device_content_group)
    val channelLost = itemView.findViewById<TextView>(R.id.device_channel_lost)
    val channelList = itemView.findViewById<RecyclerView>(R.id.device_channel_list)
    val channelAdapter = ChannelAdapter(navController)

    init {
        channelList.adapter = channelAdapter
        val manager = LinearLayoutManager(itemView.context)
        manager.orientation = HORIZONTAL
        channelList.layoutManager = manager
//        val decoration = DividerItemDecoration(navController.context,  DividerItemDecoration.HORIZONTAL).apply {
//            ResourcesCompat.getDrawable(navController.context.resources, R.drawable.shape_list_hor_divider, null)?.let {
//                setDrawable(it)
//            }
//        }
//        channelList.addItemDecoration(decoration)
    }

    private var chanelData: List<DeviceChanel>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setChannelList(channelList: List<DeviceChanel>?){
        Log.d(TAG, "setChannelList: ")
        this.chanelData = channelList



        channelAdapter.setChannels(channelList)
        channelAdapter.notifyDataSetChanged()
    }

    fun setDeviceId(deviceId: String?){
        channelAdapter.bindDeviceId(deviceId)
    }


}