package com.ly.wvp.device.channel

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ly.wvp.R
import com.ly.wvp.auth.NetError
import com.ly.wvp.data.model.DeviceChanel
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.device.play.LiveDetailPlayerFragmentArgs
import com.ly.wvp.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class ChannelAdapter(private val navController: NavController): Adapter<ChannelAdapter.ChannelHolder>() {

    companion object{
        private const val TAG = "ChannelAdapter"
    }
    private var deviceId: String? = null

    private var channels: List<DeviceChanel>? = null

    private val snapLoader by lazy {
        ChannelSnapLoader(DataStorage.getInstance(navController.context).getConfig())
    }

    fun setChannels(deviceChannel: List<DeviceChanel>?){
        channels = deviceChannel
    }

    fun bindDeviceId(id: String?){
        deviceId = id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHolder {
        Log.d(TAG, "onCreateViewHolder: ChannelHolder")
        //分屏/弹出窗口时 does not have a NavController set
//        navController = parent.findNavController()
        val holder = ChannelHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_device_channel_item, parent, false))
        holder.chanelItem.setOnClickListener{
            val channelIdChar = holder.channelId.text
            if (channelIdChar == null || deviceId == null){
                R.string.device_not_found.shortToast(parent.context)
                return@setOnClickListener
            }
            navController.navigate(
                R.id.liveDetailPlayerFragment,
                LiveDetailPlayerFragmentArgs(deviceId!!, channelIdChar.toString()).toBundle(),
                NavOptions.Builder().setPopUpTo(R.id.liveDetailPlayerFragment, true).build()
            )
        }
        return holder
    }

    override fun getItemCount(): Int {
        return channels?.size ?: 0
    }

    override fun onBindViewHolder(holder: ChannelHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ")
        channels?.let { it ->
//            holder.channelLoading.visibility = GONE
//            holder.channelContent.visibility = VISIBLE
            val chanel = it[position]
            holder.channelId.text = chanel.getChannelId()
            holder.hasAudio.text = if (chanel.isHasAudio()) "音频开启" else "音频关闭"
            holder.manuFactory.text = chanel.getManufacture()
//            holder.snapShot.text = chanel.getChannelId()
            chanel.getChannelId()?.let { chanelId->
                bindSnapAsync(holder, chanelId)
            }

        }?: {
            Log.e(TAG, "onBindViewHolder: channels is null")
//            holder.channelLoading.visibility = VISIBLE
//            holder.channelContent.visibility = GONE
        }

    }

    private fun bindSnapAsync(holder: ChannelHolder, channelId: String) {
        deviceId?.let {
            CoroutineScope(Dispatchers.IO).launch {
                //网络异常
                try {
                    val byteArray = snapLoader.loadSnap(it, channelId)
                    if (byteArray != null){
                        val reqH = holder.snapShot.measuredHeight
                        val reqW = holder.snapShot.measuredWidth
                        val bitmap = snapLoader.getBitmapWithReqSize(byteArray, reqW, reqH)
                        if (bitmap != null){
                            launch(Main){
                                holder.snapShot.setImageBitmap(bitmap)
                            }
                        }else{
                            Log.d(TAG, "bindSnapAsync: load snap failed bitmap == null")
                        }
                    }
                    else{
                        Log.w(TAG, "bindSnapAsync: load snap failed stream null")
                    }
                }
                catch (e: Exception){
                    Log.w(TAG, "bindSnapAsync: ${e.message}")
                    holder.itemView.context?.let {
                        shortToast("${NetError.INTERNET_INVALID}:${e.message?: "Net Error"}",  it)
                    }
                }
            }
        }

    }

    private fun shortToast(cause: String, context: Context){
        val appContext = context.applicationContext ?: return
        CoroutineScope(Main).launch {
            cause.shortToast(appContext)
        }
    }

    class ChannelHolder(item: View): ViewHolder(item) {

        val chanelItem = item
        val channelId = item.findViewById<TextView>(R.id.device_channel_id)
        val hasAudio = item.findViewById<TextView>(R.id.has_audio)
        val manuFactory = item.findViewById<TextView>(R.id.device_channel_manufactory)
        val snapShot = item.findViewById<ImageView>(R.id.device_channel_snapshot)

//        val channelContent = item.findViewById<Group>(R.id.channel_content)
//        val channelLoading = item.findViewById<ProgressBar>(R.id.loading_channel)

    }
}