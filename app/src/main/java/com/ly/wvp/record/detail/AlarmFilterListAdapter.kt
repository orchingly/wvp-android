package com.ly.wvp.record.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.ly.wvp.R

/**
 * 报警事件筛选
 */
class AlarmFilterListAdapter(private val mAlarmCheckListener: AlarmCheckListener): RecyclerView.Adapter<AlarmFilterListAdapter.AlarmHolder>() {

    private val mFilterOptions: ArrayList<String> = ArrayList()
    private val mCheckList =  ArrayList<String>()
    init {
        mFilterOptions.add("正常")
        mFilterOptions.add("移动")
        mFilterOptions.add("其他")
    }

    class AlarmHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val alarmCheckBox:CheckBox = itemView.findViewById(R.id.alarm_checkbox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AlarmHolder {

        return AlarmHolder(LayoutInflater.from(parent.context).inflate(R.layout.alarm_filter_list_item, parent, false))
    }

    override fun getItemCount(): Int {

        return 0
    }

    override fun onBindViewHolder(holder: AlarmHolder, position: Int) {
        holder.alarmCheckBox.text = mFilterOptions[position]
    }

    interface AlarmCheckListener{
        fun onCheckUpdate()
    }
}