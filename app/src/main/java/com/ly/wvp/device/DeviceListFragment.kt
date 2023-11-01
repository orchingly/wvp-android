package com.ly.wvp.device

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ly.wvp.R
import com.ly.wvp.auth.NetError
import com.ly.wvp.auth.NetError.Companion.AUTH_FAILED
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.data.storage.SettingsConfig
import com.ly.wvp.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 国标设备
 */
class DeviceListFragment : Fragment() {
    companion object{
        private const val TAG = "DeviceListFragment"
    }
    private lateinit var viewModel: DeviceListViewModel

    private lateinit var deviceListView: RecyclerView

    private lateinit var deviceLoading: ProgressBar

    private lateinit var deviceAdapter: DeviceAdapter

    private lateinit var emptyDevice: TextView

    private lateinit var navController: NavController

    private lateinit var storage: DataStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gb_device_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        viewModel = ViewModelProvider(this)[DeviceListViewModel::class.java]
        deviceListView = view.findViewById(R.id.device_list)
        deviceLoading = view.findViewById(R.id.device_list_loading)
        emptyDevice = view.findViewById(R.id.empty_device_list_title)
        deviceAdapter = DeviceAdapter()
        deviceListView.layoutManager = LinearLayoutManager(context)
        deviceListView.adapter = deviceAdapter
        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL).apply {
            ResourcesCompat.getDrawable(resources, R.drawable.shape_list_divider, null)?.let {
                setDrawable(it)
            }
        }
        deviceListView.addItemDecoration(decoration)

        storage = DataStorage.getInstance(requireContext())
        //没有配置服务器返回登录
        checkServerConfig(storage.getConfig())
        viewModel.setConfig(storage.getConfig())

        viewModel.getDeviceLiveData().observe(viewLifecycleOwner) {
            Log.d(TAG, "onViewCreated: device list loaded total: ${it.size}")
            if (it.isEmpty()){
                deviceListView.visibility = GONE
                deviceLoading.visibility = GONE
                emptyDevice.visibility = VISIBLE
            }
            else{
                deviceListView.visibility = VISIBLE
                deviceLoading.visibility = GONE
                emptyDevice.visibility = GONE
            }
            deviceAdapter.setDeviceList(it)
            deviceAdapter.notifyDataSetChanged()
            //加载channel
            it.forEach {device ->
                device.getDeviceId()?.let {id ->
                    viewModel.loadDeviceChannelList(id)
                }
            }
        }

        viewModel.getChannelList().observe(viewLifecycleOwner){
            Log.d(TAG, "onViewCreated: device channel loaded total: ${it.channels().size}")
            val index = deviceAdapter.setDeviceChannelList(it.queryId(), it.channels())
            if (index >= 0){
                deviceAdapter.notifyItemChanged(index)
            }
        }

        viewModel.getError().observe(viewLifecycleOwner){
            checkError(it)
        }

        viewModel.loadDeviceList()
        deviceListView.visibility = GONE
        deviceLoading.visibility = VISIBLE
        emptyDevice.visibility = GONE
        //设置导航栏状态
        activateBottomNav(view)
    }

    private fun checkServerConfig(config: SettingsConfig) {
        if (config.ip.isEmpty()){
           goLoginFragment()
        }
    }

    private fun checkError(error: NetError) {
        deviceListView.visibility = GONE
        deviceLoading.visibility = GONE
        emptyDevice.visibility = VISIBLE
        shortToast(error.toString())
        //认证失败,跳转登录
        //所有异常均跳转到登录
        goLoginFragment()
    }

    private fun goLoginFragment(){
        navController.navigate(R.id.loginFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build())
    }


    private fun shortToast(cause: String){
        val appContext = context?.applicationContext ?: return
        CoroutineScope(Dispatchers.Main).launch {
            cause.shortToast(appContext)
        }
    }

    private fun activateBottomNav(view: View){
        view.findViewById<Button>(R.id.tab_device).isSelected = true
        view.findViewById<Button>(R.id.tab_record).isSelected = false
        view.findViewById<Button>(R.id.tab_settings).isSelected = false

        view.findViewById<Button>(R.id.tab_record).setOnClickListener {
            navController.navigate(R.id.cloudRecordListFragment,
                null,
                //cloudRecordListFragment及以上全部出栈
                NavOptions.Builder().setPopUpTo(R.id.cloudRecordListFragment, true).build()
            )
        }

        view.findViewById<Button>(R.id.tab_settings).setOnClickListener {
            navController.navigate(R.id.settingsFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.settingsFragment, true).build()
            )
        }
    }

}