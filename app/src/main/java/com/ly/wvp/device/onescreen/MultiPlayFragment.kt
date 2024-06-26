package com.ly.wvp.device.onescreen

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.donkingliang.groupedadapter.decoration.GroupedLinearItemDecoration
import com.ly.wvp.R
import com.ly.wvp.data.model.Device
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.device.play.MultiPlayManager
import kotlin.math.min

/**
 * 多设备/通道同屏播放
 */
class MultiPlayFragment : Fragment() {

    companion object {
        private const val TAG = "MultiPlayFragment"
    }

    private lateinit var viewModel: MultiPlayViewModel

    /**
     * 处理多选数据
     */
    private lateinit var filterViewModel: DeviceFilterViewModel

    /**
     * mask
     */
    private lateinit var mSelectorBg: Group

    /**
     * 弹窗空顶部白区域
     */
    private lateinit var mDialogBlank: View

    /**
     * 选项按钮
     */
    private lateinit var mSelectorBtn: ImageView

    private lateinit var mTitle: TextView

    /**
     * 播放器列表
     */
    private lateinit var multiPlayList: RecyclerView

    private lateinit var playListAdapter: PlayListAdapter

    private lateinit var playerHelper: ScrollPlayerHelper

    /**
     * 设备选择列表
     */
    private lateinit var mDeviceSelectList: RecyclerView

    /**
     * 确定&取消按钮
     */
    private lateinit var mSelectCancel: Button
    private lateinit var mSelectConfirm: Button

    /**
     * 选中项列表
     * 播放列表
     */
    private val mSelection = ArrayList<SelectionItem>()

    /**
     * 设备备选列表
     * 保证和服务器返回的数据顺序一致
     */
    private val mDeviceList = ArrayList<Device>()

    /**
     * 存储配置
     */
    private val mStorage: DataStorage by lazy {
        DataStorage.getInstance(requireContext())
    }

    private lateinit var navController: NavController

//    private lateinit var mSelectionAdapter: DeviceListSelectAdapter

    private lateinit var mDeviceFilterAdapter: ChannelGroupAdapter

    private lateinit var mNaviMenu: Group

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_multi_play, container, false)
    }

    override fun onViewCreated(parent: View, savedInstanceState: Bundle?) {
        super.onViewCreated(parent, savedInstanceState)
        viewModel = ViewModelProvider(this)[MultiPlayViewModel::class.java]
        filterViewModel = ViewModelProvider(this)[DeviceFilterViewModel::class.java]

        mSelectorBg = parent.findViewById(R.id.group_device_selector)
        mSelectorBtn = parent.findViewById(R.id.cloud_record_filter)
        mSelectorBtn.visibility = VISIBLE
        mTitle = parent.findViewById(R.id.action_bar_content_title)
        mTitle.setText(R.string.multi_play_device_select)
        parent.findViewById<ImageView>(R.id.action_bar_back_img).visibility = GONE
        multiPlayList = parent.findViewById(R.id.multi_play_list)
        mDeviceSelectList = parent.findViewById(R.id.device_select_list)
        mSelectCancel = parent.findViewById(R.id.device_s_cancel)
        mSelectConfirm = parent.findViewById(R.id.device_s_confirm)
        mDialogBlank = parent.findViewById(R.id.device_select_bg_top)
        mNaviMenu = parent.findViewById(R.id.nav_bottom_group)
        navController = findNavController()
        initData()
        initClickListener()
        bindAdapter()
        bindSelectionConfirmListener()
        activateBottomNav(parent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MultiPlayManager.clearAllVideo()
    }

    override fun onPause() {
        super.onPause()
        MultiPlayManager.onPauseAll()
    }

    override fun onResume() {
        super.onResume()
        MultiPlayManager.onResumeAll()
    }

    private fun initClickListener() {
        mSelectorBtn.setOnClickListener {
            showDeviceSelectionDialog()
        }
        mDialogBlank.setOnClickListener {
            hideDeviceSelectionDialog()
        }
        filterViewModel.getDeviceFilterOptions().observe(viewLifecycleOwner){
            handleDeviceFilterOptionChanged(it)
        }
    }

    private fun handleDeviceFilterOptionChanged(selections: List<SelectionItem>) {
        Log.d(TAG, "handleDeviceFilterOptionChanged: update selections")
        mSelection.clear()
        mSelection.addAll(selections)
        hideDeviceSelectionDialog(true)
        updatePlayList()
    }

    private fun initData() {
        mSelection.clear()
        mSelection.addAll(mStorage.getMultiPlayDeviceList())
        //多设备测试
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
//        mSelection.addAll(mStorage.getMultiPlayDeviceList())
        Log.d(TAG, "initData: selection size addAll = ${mSelection.size}" )
        mDeviceList.addAll(mStorage.getDeviceList())
        checkValidSelections()
        filterViewModel.initFilterOptions(mSelection, mStorage)
        Log.d(TAG, "initData: selection checkValidSelections size = ${mSelection.size}" )
    }

    /**
     * 选项列表的数据必须存在于DeviceChannelList中
     */
    private fun checkValidSelections() {
        val fullSelectionList = ArrayList<SelectionItem>()
        for (i in mDeviceList.indices){
            val device = mDeviceList[i]
            device.getChannelList()?.let {
                for (j in it.indices){
                    device.getDeviceId()?.let { dId ->
                        it[j].getChannelId()?.let {cId ->
                            val selection = SelectionItem(dId, cId)
                            fullSelectionList.add(selection)
                        }
                    }
                }
            }
        }
        val iterator = mSelection.iterator()
        while (iterator.hasNext()){
            val s = iterator.next()
            //之前选中的项在服务器最新返回的设备名单中已不存在,删除旧元素
            if (!fullSelectionList.contains(s)){
                iterator.remove()
            }
        }
    }

    private fun bindSelectionConfirmListener() {
        mSelectCancel.setOnClickListener {
            //消去弹窗
            hideDeviceSelectionDialog()
        }
        mSelectConfirm.setOnClickListener {
            filterViewModel.saveChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updatePlayList() {
        val original = playListAdapter.getPlayList()

        //全部删除
        if (mSelection.isEmpty()){
            if (original.isNotEmpty()){
                Log.d(TAG, "updatePlayList: remove all")
                playListAdapter.bindSelection(mSelection)
                playListAdapter.notifyItemRangeRemoved(0, original.size)
            }
            //else 啥也没变化
            return
        }
        //从无到有
        if (original.isEmpty()){
            if (mSelection.isNotEmpty()){
                Log.d(TAG, "updatePlayList: add all")
                playListAdapter.bindSelection(mSelection)
                playListAdapter.notifyItemRangeInserted(0, mSelection.size)
            }
            //else 啥也没变化
            return
        }
        val endLen = min(mSelection.size, original.size)
        val changeList = ArrayList<Int>()
        for (i in 0 until endLen){
            if (mSelection[i] != original[i]){
                changeList.add(i)
            }
        }

        Log.d(TAG, "updatePlayList: change ${changeList.size}")
        if (mSelection.size > original.size){
            //纯增加
            if (changeList.isEmpty()){
                Log.d(TAG, "updatePlayList: add")
                playListAdapter.bindSelection(mSelection)
                playListAdapter.notifyItemRangeInserted(original.size, (mSelection.size - original.size))
                return
            }
        }

        if (mSelection.size < original.size){
            //纯删除
            if (changeList.isEmpty()){
                Log.d(TAG, "updatePlayList: delete")
                playListAdapter.bindSelection(mSelection)
                playListAdapter.notifyItemRangeRemoved(mSelection.size - 1, (original.size - mSelection.size))
                return
            }
        }

        if (mSelection.size == original.size){
            //啥也没变
            if (changeList.isEmpty()){
                Log.d(TAG, "updatePlayList: nothing change")
                return
            }
        }

        Log.d(TAG, "updatePlayList: update all")
        playListAdapter.bindSelection(mSelection)
        playListAdapter.notifyDataSetChanged()
    }

    private fun bindAdapter() {
        //初始化选项列表
        bindDeviceFilterAdapter()

        //初始化播放列表
        playerHelper = ScrollPlayerHelper()
        playListAdapter = PlayListAdapter(requireContext(), ArrayList(mSelection), mStorage.getConfig(), playerHelper)
        multiPlayList.adapter = playListAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        multiPlayList.layoutManager = layoutManager
        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL).apply {
            ResourcesCompat.getDrawable(resources, R.drawable.shape_play_list_divider, null)?.let {
                setDrawable(it)
            }
        }
        multiPlayList.addItemDecoration(decoration)
        multiPlayList.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                playerHelper.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val firstVisiblePos = layoutManager.findFirstVisibleItemPosition()
                val lastVisiblePos = layoutManager.findLastVisibleItemPosition()
                if (firstVisiblePos == NO_POSITION || lastVisiblePos == NO_POSITION){
                    Log.d(TAG, "onScrolled: find pos failed -1")
                    return
                }
//                Log.d(TAG, "onScrolled: $firstVisiblePos, $lastVisiblePos, $dy")
                playerHelper.onScroll(recyclerView, firstVisiblePos, lastVisiblePos, lastVisiblePos - firstVisiblePos)
            }
        })
    }

    private val mCheckListener by lazy {
        object : ChannelGroupAdapter.CheckChangeListener {
            override fun handleChecked(groupPosition:Int, childPosition: Int, isChecked: Boolean) {
                handleChannelChecked(groupPosition, childPosition, isChecked)
            }
        }
    }

    private fun bindDeviceFilterAdapter() {
        mDeviceFilterAdapter = ChannelGroupAdapter(requireContext(), mDeviceList, ArrayList(mSelection), mCheckListener)
        mDeviceSelectList.adapter = mDeviceFilterAdapter
        mDeviceSelectList.layoutManager = LinearLayoutManager(requireContext())
        val d = GroupedLinearItemDecoration(mDeviceFilterAdapter,20, null,20,null,20,null);
        mDeviceSelectList.addItemDecoration(d)
    }

    private fun handleChannelChecked(
        groupPosition: Int,
        childPosition: Int,
        checked: Boolean
    ) {
        val device = mDeviceList[groupPosition]
        device.getChannelList()?.let {
            val channel = it[childPosition].getChannelId()
            Log.d(TAG, "onChildClick: ${device.getDeviceId()} - $channel checked: ${checked}")
            filterViewModel.onChannelClicked(checked, device.getDeviceId(), channel)
        }
    }


    private fun hideDeviceSelectionDialog(shouldSave: Boolean = false){
        //需要保存
//        if (!shouldSave){
//            val cache = mStorage.getMultiPlayDeviceList()
//            //选项变化,选项回退, 否则下次打开选中项和播放列表不一致
//            if (cache != mSelection){
//                Log.d(TAG, "hideDeviceSelectionDialog: selection rollback")
//                mSelection.clear()
//                mSelection.addAll(cache)
//            }
//        }
        //消去弹窗
        mSelectorBg.visibility = GONE
        //显示底部导航栏
//        mNaviMenu.visibility = VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDeviceSelectionDialog(){
        //同步选中列表到UI
        mDeviceFilterAdapter.syncCheckedList(mSelection)
        //同步选中列表到view model
        filterViewModel.syncFilterOptions(mSelection)
        mDeviceFilterAdapter.notifyDataChanged()
        mSelectorBg.visibility = VISIBLE
    }

    private fun activateBottomNav(view: View){
        view.findViewById<Button>(R.id.tab_device).isSelected = false
        view.findViewById<Button>(R.id.tab_record).isSelected = false
        view.findViewById<Button>(R.id.tab_settings).isSelected = false
        view.findViewById<Button>(R.id.tab_multi_device_play).isSelected = true

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

        view.findViewById<Button>(R.id.tab_device).setOnClickListener {
            navController.navigate(R.id.deviceListFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.deviceListFragment, true).build()
            )
        }
    }
}