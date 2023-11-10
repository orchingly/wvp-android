package com.ly.wvp.record

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ly.wvp.R
import com.ly.wvp.auth.NetError
import com.ly.wvp.data.model.MediaServerItem
import com.ly.wvp.data.model.PageInfo
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CloudRecordListFragment : Fragment() {

    companion object {
        private const val TAG = "CloudRecordListFragment"
    }

    private lateinit var viewModel: CloudRecordListViewModel
    private lateinit var navController: NavController
    private lateinit var storage: DataStorage
    private lateinit var mediaSelector: Spinner
    private lateinit var recordList: RecyclerView
    private lateinit var recordAdapter: CloudRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cloud_record_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = DataStorage.getInstance(requireContext())
        viewModel = ViewModelProvider(this)[CloudRecordListViewModel::class.java]
        viewModel.setConfig(storage.getConfig())
        navController = findNavController()
        mediaSelector = view.findViewById(R.id.media_server_list)
        recordList = view.findViewById(R.id.cloud_record_list)
        recordAdapter = CloudRecordAdapter()
        //初始化导航栏
        activateBottomNav(view)
        viewModel.getMediaServerLiveData().observe(viewLifecycleOwner){
            //初始化MediaServer下拉列表
            handleMediaServerList(it)
        }
        viewModel.getError().observe(viewLifecycleOwner){
            handleError(it)
        }
        viewModel.getCloudRecordPageInfo().observe(viewLifecycleOwner){
            //云录像记录返回处理
            handleRecordPageInfo(it)
        }
        //请求media server列表
        viewModel.requestRemoteMediaServer()
        recordList.adapter = recordAdapter
        recordList.layoutManager = LinearLayoutManager(requireContext())
        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL).apply {
            ResourcesCompat.getDrawable(resources, R.drawable.shape_list_divider, null)?.let {
                setDrawable(it)
            }
        }
        recordList.addItemDecoration(decoration)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleRecordPageInfo(recordPageInfo: PageInfo<Map<String, String>>) {
        recordPageInfo.list?.let {
            if (it.isEmpty()){
                handleEmptyRecordList()
            }
            else{
                recordAdapter.updateRecordData(recordPageInfo)
                recordAdapter.notifyDataSetChanged()
            }

        }?: kotlin.run {
            handleNullRecordList()
        }

    }

    private fun handleNullRecordList() {
        Log.w(TAG, "handleRecordPageInfo: record list is null")
        handleEmptyRecordList()
    }

    private fun handleEmptyRecordList() {

    }

    private fun handleMediaServerList(serverItems: List<MediaServerItem>) {
        val items = ArrayList<String>()
        serverItems.forEach {
            it.id?.let { id->
                items.add(id)
            }
        }
        items.add("1111111111111")
        items.add("1111111111112")
        val item1 = MediaServerItem()
        item1.id = "1111111111111"
        (serverItems as ArrayList).add(item1)

        val item2 = MediaServerItem()
        item2.id = "1111111111112"
        serverItems.add(item2)

        val adapter = ArrayAdapter(requireContext(), R.layout.media_server_item, items)
        adapter.setDropDownViewResource(R.layout.server_item_drop_down)
        mediaSelector.adapter = adapter
        mediaSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (checkPositionValid(position, items)){
                    val server = items[position]
                    Log.d(TAG, "onItemSelected: $server pos:$position")
                    recordAdapter.updateMediaServer(serverItems[position])
                    viewModel.requestCloudRecord(server)
                }
                else{
                    Log.w(TAG, "onItemSelected: invalid selection, s:$position, t:${items.size}", )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        mediaSelector.setSelection(0)
    }

    private fun checkPositionValid(position: Int, items: java.util.ArrayList<String>): Boolean {
        return position >= 0 && position < items.size
    }

    private fun activateBottomNav(view: View){
        view.findViewById<Button>(R.id.tab_device).isSelected = false
        view.findViewById<Button>(R.id.tab_multi_device_play).isSelected = false
        view.findViewById<Button>(R.id.tab_record).isSelected = true
        view.findViewById<Button>(R.id.tab_settings).isSelected = false

        view.findViewById<Button>(R.id.tab_device).setOnClickListener {
            navController.navigate(R.id.deviceListFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.deviceListFragment, true).build()
            )
        }

        view.findViewById<Button>(R.id.tab_settings).setOnClickListener {
            navController.navigate(R.id.settingsFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.settingsFragment, true).build()
            )
        }

        view.findViewById<Button>(R.id.tab_multi_device_play).setOnClickListener {
            navController.navigate(R.id.multiPlayFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.multiPlayFragment, true).build()
            )
        }
    }

    private fun handleError(error:NetError){
        //TODO:加载失败,清空列表
        shortToast(error.toString())
    }

    private fun shortToast(cause: String){
        val appContext = context?.applicationContext ?: return
        CoroutineScope(Dispatchers.Main).launch {
            cause.shortToast(appContext)
        }
    }
}