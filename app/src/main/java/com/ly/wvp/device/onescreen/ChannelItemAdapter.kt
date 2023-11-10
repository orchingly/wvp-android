package com.ly.wvp.device.onescreen

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import com.ly.wvp.R

class ChannelItemAdapter(private val mContext: Context,
                         private val deviceId: String,
                         private val channelList: List<String>,
                         private val selectionList: ArrayList<SelectionItem> ): BaseAdapter() {

    companion object{
        const val TAG = "ChannelItemAdapter"
    }

    override fun getCount(): Int {
        return channelList.size
    }

    override fun getItem(position: Int): Any {
       return channelList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val channelId = channelList[position]
        convertView?.let {
            val holder = it.tag as ChannelHolder
            holder.channelCheck?.text = channelId
            holder.channelCheck?.setOnCheckedChangeListener { target, isChecked ->
               bindCheckListener(target as CheckBox, isChecked)
            }
            //绑定初始选中状态
            holder.channelCheck?.isChecked = selectionList.contains(SelectionItem(deviceId, channelId))
            return it
        }?: kotlin.run {
            val itemView = LayoutInflater.from(mContext).inflate(R.layout.device_selector_item_channel_list_item, parent, false)
            val holder = ChannelHolder()
            holder.channelCheck = itemView.findViewById(R.id.channel_check)
            holder.channelCheck?.text = channelId
            holder.channelCheck?.setOnCheckedChangeListener { target, isChecked ->
                bindCheckListener(target as CheckBox, isChecked)
            }

            //绑定初始选中状态
            holder.channelCheck?.isChecked = selectionList.contains(SelectionItem(deviceId, channelId))

            itemView.tag = holder
            return itemView
        }
    }

    /**
     * 选中添加,反选删除
     */
    private fun bindCheckListener(target: CheckBox, isChecked: Boolean){
        val channelId = target.text.toString()
        if (isChecked){
            val item = SelectionItem(deviceId, channelId)
            if (!selectionList.contains(item)){
                Log.d(TAG, "bindCheckListener: new select channel: $channelId")
                selectionList.add(item)
            }else{
                Log.w(TAG, "bindCheckListener: already added channel: $channelId")
            }
        }
        else{
            Log.d(TAG, "bindCheckListener: remove channel: $channelId")
            selectionList.remove(SelectionItem(deviceId, channelId))
        }
    }

    class ChannelHolder{
        var channelCheck: CheckBox? = null
    }
}