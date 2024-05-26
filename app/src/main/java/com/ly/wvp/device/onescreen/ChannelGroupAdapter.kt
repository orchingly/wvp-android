package com.ly.wvp.device.onescreen

import android.content.Context
import android.util.Log
import android.widget.CheckBox
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter
import com.donkingliang.groupedadapter.holder.BaseViewHolder
import com.ly.wvp.R
import com.ly.wvp.data.model.Device

class ChannelGroupAdapter(context: Context,
                          private val mDeviceList: ArrayList<Device>,
                          private val mCheckedList: ArrayList<SelectionItem>,
                          private val mCheckListener: CheckChangeListener
) : GroupedRecyclerViewAdapter(context) {

    init {


    }
    companion object{
        private const val TAG = "ChannelGroupAdapter"
    }
    fun updateDeviceList(deviceList: ArrayList<Device>, checkList: ArrayList<SelectionItem>){
        mDeviceList.clear()
        mCheckedList.clear()
        mDeviceList.addAll(deviceList)
        mCheckedList.addAll(checkList)
        notifyDataChanged()
    }

    /**
     * 每次打开选项列表都需要同步一下，否则选中数据错乱
     */
    fun syncCheckedList(checkList: ArrayList<SelectionItem>){
        mCheckedList.clear()
        mCheckedList.addAll(checkList)
        notifyDataChanged()
    }

    override fun getGroupCount(): Int {
        return mDeviceList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return mDeviceList[groupPosition].getChannelList()?.size ?: 0
    }

    override fun hasHeader(groupPosition: Int): Boolean {
        return true
    }

    override fun hasFooter(groupPosition: Int): Boolean {
        return false
    }

    override fun getHeaderLayout(viewType: Int): Int {
        return R.layout.deivice_header_item
    }

    override fun getFooterLayout(viewType: Int): Int {
        return -1
    }

    override fun getChildLayout(viewType: Int): Int {
        return R.layout.device_selector_item_channel_list_item
    }

    override fun onBindHeaderViewHolder(holder: BaseViewHolder, groupPosition: Int) {
        holder.setText(R.id.select_device_id, mDeviceList[groupPosition].getDeviceId())
    }

    override fun onBindFooterViewHolder(holder: BaseViewHolder?, groupPosition: Int) {
        //No footer
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder,
        groupPosition: Int,
        childPosition: Int
    ) {
        val device = mDeviceList[groupPosition]
        val channel = device.getChannelList()?.get(childPosition)
        channel?.let {
            val checkBox = holder.get<CheckBox>(R.id.channel_check)
            val checkItem = SelectionItem(device.getDeviceId()!!, it.getChannelId()!!)
            checkBox.text = it.getChannelId()
            checkBox.isChecked = mCheckedList.contains(checkItem)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                mCheckListener.handleChecked(groupPosition, childPosition, isChecked)
            }

        }?: kotlin.run {
            Log.w(TAG, "onBindChildViewHolder: empty channels in device ${device.getDeviceId()}")
        }
    }

    interface CheckChangeListener{
        fun handleChecked(groupPosition:Int, childPosition:Int, isChecked: Boolean)
    }
}