package com.ly.wvp.record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ly.wvp.R
import com.ly.wvp.auth.ResponseBody.Companion.APP
import com.ly.wvp.auth.ResponseBody.Companion.STREAM
import com.ly.wvp.auth.ResponseBody.Companion.TIME
import com.ly.wvp.data.model.MediaServerItem
import com.ly.wvp.data.model.PageInfo
import com.ly.wvp.record.detail.CloudRecordDetailFragmentArgs

class CloudRecordAdapter: Adapter<CloudRecordAdapter.RecordHolder>() {

    private val recordData = PageInfo<Map<String, String>>()

    private var mediaServer: MediaServerItem = MediaServerItem()

    fun updateRecordData(data: PageInfo<Map<String, String>>){
        recordData.update(data)
    }

    fun updateMediaServer(server: MediaServerItem){
        mediaServer = server
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val holder = RecordHolder(LayoutInflater.from(parent.context).inflate(R.layout.record_holder_item, parent, false))
        val navController = parent.findNavController()
        holder.itemView.setOnClickListener{
            openRecordDetail(navController, holder)
        }
        return holder
    }

    private fun openRecordDetail(nav: NavController, holder: RecordHolder) {
        nav.navigate(R.id.cloudRecordDetailFragment,
            CloudRecordDetailFragmentArgs(holder.app.text.toString(),
                holder.stream.text.toString(),
                mediaServer.id!!,
                mediaServer.streamIp!!,
                mediaServer.httpPort,
                mediaServer.httpSSlPort
            ).toBundle(),
            //cloudRecordListFragment及以上全部出栈
            NavOptions.Builder().setPopUpTo(R.id.cloudRecordDetailFragment, true).build()
        )
    }

    override fun getItemCount(): Int {
        return recordData.list?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        recordData.list?.let {
            val item = it[position]
            holder.app.text = item[APP]
            holder.stream.text = item[STREAM]
            holder.creatTime.text = item[TIME]
        }
    }



    class RecordHolder(item: View): RecyclerView.ViewHolder(item){
        val app = item.findViewById<TextView>(R.id.record_app)
        val stream = item.findViewById<TextView>(R.id.record_stream_id)
        val creatTime = item.findViewById<TextView>(R.id.record_create_time)
//        val action = item.findViewById<Button>(R.id.record_act_go_btn)
    }
}