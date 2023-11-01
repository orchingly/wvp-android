package com.ly.wvp.record.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ly.wvp.R
import com.ly.wvp.auth.NetError
import com.ly.wvp.calendar.Calendar
import com.ly.wvp.calendar.CalendarLayout
import com.ly.wvp.calendar.CalendarView
import com.ly.wvp.data.model.StreamDetectionItem
import com.ly.wvp.data.model.StreamDetectionItem.Companion.EVENT_MOVE
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.record.play.SampleCoverVideo
import com.ly.wvp.util.shortToast
import com.ly.wvp.widget.progress.SectionBean
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tv.danmaku.ijk.media.player.IjkMediaPlayer

open class CloudRecordDetailFragment() : Fragment() {

    companion object {
        private const val TAG = "CloudRecordDetailFragment"
    }

    private lateinit var viewModel: CloudRecordDetailViewModel

    private lateinit var calendarView: CalendarView
    private lateinit var calendarLayout: CalendarLayout
    private lateinit var dayRecordList: RecyclerView
    private lateinit var storage: DataStorage
    private lateinit var fileAdapter: RecordFileListAdapter

    private lateinit var monthDay: TextView
    private lateinit var calendarLunar: TextView
    private lateinit var calendarYear: TextView
    private lateinit var calendarToday: TextView

    private lateinit var mActionBack: ImageView
    private lateinit var mTitleOfCalendar: TextView

    private var recordCache: CloudRecord? = null


    private lateinit var player: SampleCoverVideo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cloud_record_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CloudRecordDetailViewModel::class.java]
        calendarView = view.findViewById(R.id.calendarView)
        monthDay = view.findViewById(R.id.tv_month_day)
        calendarLunar = view.findViewById(R.id.tv_lunar)
        calendarYear = view.findViewById(R.id.tv_year)
        calendarToday = view.findViewById(R.id.tv_current_day)

        player = view.findViewById(R.id.record_detail_player)

        calendarLayout = view.findViewById(R.id.calendarLayout)
        dayRecordList = view.findViewById(R.id.record_detail_list)
        mTitleOfCalendar = view.findViewById(R.id.action_bar_content_title)
        mActionBack = view.findViewById(R.id.action_bar_back_img)
        storage = DataStorage.getInstance(requireContext())
        viewModel.setConfig(storage.getConfig())
//        initData()
        fileAdapter = RecordFileListAdapter()
        dayRecordList.adapter = fileAdapter
        dayRecordList.layoutManager = LinearLayoutManager(requireContext())

        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL).apply {
            ResourcesCompat.getDrawable(resources, R.drawable.shape_list_divider, null)?.let {
                setDrawable(it)
            }
        }
        dayRecordList.addItemDecoration(decoration)

        calendarLayout.setRecyclerView(dayRecordList)

        val app = CloudRecordDetailFragmentArgs.fromBundle(requireArguments()).app
        val stream = CloudRecordDetailFragmentArgs.fromBundle(requireArguments()).stream
        val server = CloudRecordDetailFragmentArgs.fromBundle(requireArguments()).server
        val ip = CloudRecordDetailFragmentArgs.fromBundle(requireArguments()).serverIp
        val port = CloudRecordDetailFragmentArgs.fromBundle(requireArguments()).httpPort
        val sslPort = CloudRecordDetailFragmentArgs.fromBundle(requireArguments()).httpsPort
        val enableTls = storage.getConfig().enableTls

        viewModel.getRecordCalendar().observe(viewLifecycleOwner){
            //将有录像的日期标记出来
            showRecordDateToCalendar(it)
            //加载当天的录像文件记录
            loadRecordListOfCurrentDay(app, stream, server)
        }
        viewModel.getRecordFileList().observe(viewLifecycleOwner){
            Log.d(TAG, "onViewCreated: getRecordFileList changed")
            handleRecordList(it)
            if (it.isNotEmpty()){
                val record = it.first()
                viewModel.requestAction(record.calendar, record.app, record.stream)
            }
        }
        viewModel.getRecordActionList().observe(viewLifecycleOwner){
            //处理录像事件:移动,人形
            handleRecordAction(it)
        }
        viewModel.getError().observe(viewLifecycleOwner){
            handleError(it)
        }

        viewModel.requestRecordCalendar(app, stream, server)
        calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar?) {

            }

            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                //刷新头部日历
                refreshCalendarHeader(calendar)
                //空日历
                if (calendar.scheme.isNullOrEmpty()){
                    handleRecordList(ArrayList())
                    Log.w(TAG, "onCalendarSelect: no record")
                    return
                }
                //日期切换重新请求记录
                viewModel.requestRecordFileList(calendar, app, stream, server)

            }
        })

        val year = calendarView.curYear
        val month = calendarView.curMonth
        val day = calendarView.curDay
        refreshCalendarHeader(calendarView.selectedCalendar)
//        monthDay.setOnClickListener{
//            if (!calendarLayout.isExpand) {
//                calendarLayout.expand()
//                return@setOnClickListener
//            }
//            calendarView.showYearSelectLayout(year)
//            calendarLunar.visibility = View.GONE
//            calendarYear.visibility = View.GONE
//            monthDay.text = year.toString()
//        }


        mTitleOfCalendar.setOnClickListener{
            selectYearOrExpand(year)
        }
        mActionBack.setOnClickListener {
            findNavController().navigateUp()
        }

        calendarToday.setOnClickListener{
            calendarView.scrollToCalendar(year, month, day, true)
        }

        initVideoPlayer()
        fileAdapter.setPlayListener(object : RecordFileListAdapter.PlayListener{
            override fun onRecordPlay(record: CloudRecord) {
                val url = buildUrl(record, ip, port, sslPort, enableTls)
                Log.d(TAG, "onRecordPlay: play url = $url")
                buildGSYVideoOptionBuilder(url).build(player)

                //刷新进度条事件
                refreshEventOnProgressBar(record)

                player.startPlayLogic()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //退出全屏播放
                if (!exitFullWindow()){
                    //不是全屏播放,返回上一层fragment
                    Log.d(TAG, "handleOnBackPressed: popBackStack")
                    findNavController().popBackStack()
                }
            }
        })
    }

    /**
     * 展开日历,如果已展开,选择年份
     */
    private fun selectYearOrExpand(year: Int) {
        if (!calendarLayout.isExpand) {
            calendarLayout.expand()
            return
        }
        calendarView.showYearSelectLayout(year)
    }

    private fun refreshEventOnProgressBar(record: CloudRecord) {
        val sectionBeans: MutableList<SectionBean> = java.util.ArrayList<SectionBean>()
        if (record.eventList.isNotEmpty()){

            for (i in 0 until record.eventList.size){
                val event = record.eventList[i]

                val color = if (event.event == EVENT_MOVE){
                    R.color.progress_color_event_action_move
                } else R.color.progress_color_event_people

                //过滤极端时间点的事件
                //起始相等,终点==结尾,终点不可偏移
                //起始时间向前偏移1s
                val start = if (event.startSec == event.endSec
                    && event.endSec ==  record.duration.seconds
                    //起始>0才可偏移
                    && event.startSec > 0){
                    Log.d(TAG, "refreshEventOnProgressBar: start offset -1")
                    event.startSec - 1
                }
                else{
                    event.startSec
                }

                //事件起点==终点,1s内发生的事件
                val end = if (event.startSec == event.endSec
                    //必须小于总时长
                    && event.endSec < record.duration.seconds){
                    //终点向后偏移
                    Log.d(TAG, "refreshEventOnProgressBar: end offset +1")
                    event.endSec + 1
                }
                //事件终点==视频终点 : 终点==-1
                else if (event.endSec < 0){
                    Log.d(TAG, "refreshEventOnProgressBar: end offset end duration")
                    record.duration.seconds
                }
                else {
                    event.endSec
                }
                //不可相等
                if(start == end){
                    Log.w(TAG, "refreshEventOnProgressBar: start == end , ignore" )
                    continue
                }

                sectionBeans.add(
                    SectionBean(
                        ContextCompat.getColor(requireContext(), color),
                        event.startSec.toInt(), end.toInt(), false
                    )
                )

                Log.d(TAG, "refreshEventOnProgressBar: total: ${record.duration.seconds.toInt()}, start: ${event.startSec.toInt()}, end: $end")

            }
        }
        player.updateProgressBarSeg(sectionBeans, record.duration.seconds.toInt())
    }

    private fun loadRecordListOfCurrentDay(
        app: String,
        stream: String,
        server: String
    ) {
        val today = calendarView.selectedCalendar
        if (today.scheme.isNullOrEmpty()){
            Log.w(TAG, "showRecordDateToCalendar: no record today")
            return
        }
        viewModel.requestRecordFileList(today, app, stream, server)
    }

    private fun exitFullWindow(): Boolean {
        if (GSYVideoManager.backFromWindowFull(requireContext())) {
            Log.d(TAG, "handleTouchEvent: back true")
            return true
        }
        return false
    }

    private fun buildUrl(record: CloudRecord, ip: String, port: Int, sslPort: Int, enableTls: Boolean): String {
        val remotePort = if (enableTls){ sslPort } else port
        val http = if (enableTls) "https" else "http"
        val month = if (record.calendar.month < 10) "0${record.calendar.month}" else "${record.calendar.month}"
        val day = if (record.calendar.day < 10) "0${record.calendar.day}" else "${record.calendar.day}"
        val recordDate = "${record.calendar.year}-$month-$day"
        //http://192.168.133.176:8091/record/rtp/43000000801320000009_43000000801310000009/2023-10-07/10:28:48-10:30:48-120556.mp4
        return if (record.format == TIME_FORMAT_VERSION_1) {
            Log.d(TAG, "buildUrl: assist record version : TIME_FORMAT_VERSION_1")
            "$http://$ip:$remotePort/record/${record.app}/${record.stream}/$recordDate/${record.recordFile}"
        }
        //新版url:http://192.168.133.176:8091/record/rtp/43000000801320000009_43000000801310000009/2023-10-07/102848-103048.mp4
        else{
            val timeSp = record.recordFile.split("-")
            val from = timeSp[0].replace(":", "")
            val to = timeSp[1].replace(":", "")
            val targetSuffix = "$from-$to.mp4"
            "$http://$ip:$remotePort/record/${record.app}/${record.stream}/$recordDate/$targetSuffix"
        }
    }

    /**
     * 播放器初始化
     */
    private fun initVideoPlayer() {


        buildGSYVideoOptionBuilder("").build(player)
        //隐藏标题
        player.titleTextView.visibility = GONE
        //隐藏返回键
        player.backButton.visibility = GONE
        //设置全屏按键功能
        player.fullscreenButton.setOnClickListener {
            resolveFullBtn(player)
        }
    }


    open fun getKeyBar(): List<Int>? {
        val keybar: MutableList<Int> = java.util.ArrayList()
        keybar.add(400)
        keybar.add(800)
        return keybar
    }

    open fun getSeekBars(): List<SectionBean>? {
        val sectionBeans: MutableList<SectionBean> = java.util.ArrayList<SectionBean>()
        sectionBeans.add(
            SectionBean(
                ContextCompat.getColor(requireContext(), R.color.progress_color_event_action_move),
                300, 400, false
            )
        )
        sectionBeans.add(
            SectionBean(
                ContextCompat.getColor(requireContext(), R.color.progress_color_event_people),
                500, 800, false
            )
        )
        return sectionBeans
    }

    private fun resolveFullBtn(standardGSYVideoPlayer: StandardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(context, true, true)
    }

    private fun buildGSYVideoOptionBuilder(url: String): GSYVideoOptionBuilder {
        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT)
        return GSYVideoOptionBuilder()
            .setUrl(url)

            .setCacheWithPlay(true)
            .setIsTouchWiget(true)
            .setRotateViewAuto(true)
            .setLockLand(true)
            .setShowFullAnimation(false) //打开动画
            .setNeedLockFull(false)
            .setSeekRatio(1f)
            .setStartAfterPrepared(true)
            .setCacheWithPlay(false)
            .setPlayTag(TAG)

            .setVideoAllCallBack(object : GSYSampleCallBack() {

                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    if (!player.isIfCurrentIsFullscreen) {
                        //静音
                        GSYVideoManager.instance().isNeedMute = true
                    }
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                    //全屏不静音
                    GSYVideoManager.instance().isNeedMute = true
                    //刷新事件进度条
                    val newPlayer = objects[1] as SampleCoverVideo
                    newPlayer.refreshEventProgressBar()
                }

                override fun onEnterFullscreen(url: String?, vararg objects: Any?) {
                    super.onEnterFullscreen(url, *objects)
                    GSYVideoManager.instance().isNeedMute = false
                    //刷新事件进度条
                    val newPlayer = objects[1] as SampleCoverVideo
                    newPlayer.refreshEventProgressBar()
                    //不显示标题
//                    player.currentPlayer.titleTextView.text = objects[0].toString()
                }
            })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleRecordList(record: List<CloudRecord>) {
        //更新录像列表之前必须取消录像事件的解析任务, 否则更新recordList导致多线程不安全
        actionJob?.cancel()
        fileAdapter.updateRecordList(record)
        fileAdapter.notifyDataSetChanged()
        Log.d(TAG, "handleRecordList: record size ${record.size}")
    }

    private var actionJob: Job? = null
    @SuppressLint("NotifyDataSetChanged")
    private fun handleRecordAction(actionList: List<StreamDetectionItem>) {
        CoroutineScope(Main).launch {
            actionJob?.cancel()
            //耗时任务开启协程
            actionJob = launch(IO) {
                Log.d(TAG, "handleRecordAction: start")
                val start = System.currentTimeMillis()
                //数据多的时候耗时长,actionList.size=122, recordList.size=294 耗时566ms, 容易导致ANR
                fileAdapter.analyzeRecordAction(actionList)
                Log.d(TAG, "handleRecordAction: time used ${System.currentTimeMillis() - start}, action size : ${actionList.size}")
            }
            actionJob?.join()
            fileAdapter.notifyDataSetChanged()
        }
    }

    private fun showRecordDateToCalendar(recordDate: List<Calendar>) {
        val map: MutableMap<String, Calendar> = HashMap()
        recordDate.forEach{
            markCalendar(it, "录", -0xbf24db)
            map[it.toString()] = it
        }
        calendarView.setSchemeDate(map)
    }

    //demo
    protected fun initData() {
        val year: Int = calendarView.curYear
        val month: Int = calendarView.curMonth
        val map: MutableMap<String, Calendar> = HashMap()
        map[getSchemeCalendar(year, month, 3, -0xbf24db, "假").toString()] =
            getSchemeCalendar(year, month, 3, -0xbf24db, "假")
        map[getSchemeCalendar(year, month, 6, -0x196ec8, "事").toString()] =
            getSchemeCalendar(year, month, 6, -0x196ec8, "事")
        map[getSchemeCalendar(year, month, 9, -0x20ecaa, "议").toString()] =
            getSchemeCalendar(year, month, 9, -0x20ecaa, "议")
        map[getSchemeCalendar(year, month, 13, -0x123a93, "记").toString()] =
            getSchemeCalendar(year, month, 13, -0x123a93, "记")
        map[getSchemeCalendar(year, month, 14, -0x123a93, "记").toString()] =
            getSchemeCalendar(year, month, 14, -0x123a93, "记")
        map[getSchemeCalendar(year, month, 15, -0x5533bc, "假").toString()] =
            getSchemeCalendar(year, month, 15, -0x5533bc, "假")
        map[getSchemeCalendar(year, month, 18, -0x43ec10, "记").toString()] =
            getSchemeCalendar(year, month, 18, -0x43ec10, "记")
        map[getSchemeCalendar(year, month, 25, -0xec5310, "假").toString()] =
            getSchemeCalendar(year, month, 25, -0xec5310, "假")
        map[getSchemeCalendar(year, month, 27, -0xec5310, "多").toString()] =
            getSchemeCalendar(year, month, 27, -0xec5310, "多")
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        calendarView.setSchemeDate(map)
    }

    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int,
        color: Int,
        text: String
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = color //如果单独标记颜色、则会使用这个颜色
        calendar.scheme = text
        calendar.addScheme(Calendar.Scheme())
        calendar.addScheme(-0xff7800, "假")
        calendar.addScheme(-0xff7800, "节")
        return calendar
    }

    private fun markCalendar(calendar: Calendar, event: String, color: Int){
        calendar.schemeColor = color
        calendar.scheme = event
    }


    private fun handleError(error: NetError){
        shortToast(error.toString())
    }

    private fun shortToast(cause: String){
        val appContext = context?.applicationContext ?: return
        CoroutineScope(Dispatchers.Main).launch {
            cause.shortToast(appContext)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshCalendarHeader(calendar: Calendar){
        Log.d(TAG, "refreshCalendarHeader: $calendar")
//        calendarYear.visibility = VISIBLE
//        monthDay.text = "${calendar.month}月${calendar.day}日"
//        calendarYear.text =calendar.year.toString()
//        if (calendarView.curYear == calendar.year && calendarView.curMonth == calendar.month && calendarView.curDay == calendar.day){
//            calendarLunar.visibility = VISIBLE
//            calendarLunar.text = "今日"
//            calendarToday.text = calendar.day.toString()
//        }
//        else{
//            calendarLunar.visibility = GONE
//        }

        val title = if (calendarView.curYear == calendar.year && calendarView.curMonth == calendar.month && calendarView.curDay == calendar.day){
            "${calendar.year}年${calendar.month}月${calendar.day}日(今日)"
        }
        else{
            "${calendar.year}年${calendar.month}月${calendar.day}日"
        }
        mTitleOfCalendar.text = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        GSYVideoManager.releaseAllVideos()
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume(false)
    }

}